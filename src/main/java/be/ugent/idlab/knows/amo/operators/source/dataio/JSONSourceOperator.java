package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.ExpressionField;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.IterableField;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import be.ugent.idlab.knows.dataio.record.JSONRecord;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONObject;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Implementation of the SourceOperator using DataIO for JSON sources.
 * DataIO is written with infinite sources in mind: it provides no way to
 * inspect all variables present in the stream.
 * For this reason, this source must be provided with variables to be read and
 * included in the MappingTuple
 */
public class JSONSourceOperator extends DataIOSourceOperator {

    private final String rootIterator;
    private final List<Field> fields;
    private final Deque<SolutionMapping> solutionMappingQueue = new ArrayDeque<>();
    private transient JSONSourceIterator sourceIterator;

    public JSONSourceOperator(String operatorName, Access access, String defaultFragment,
                              String rootIterator,
                              List<Field> fields) {
        super(operatorName, access, defaultFragment);
        this.rootIterator = rootIterator;
        this.fields = fields;
        this.sourceIterator = null;
    }

    @Override
    @NonNull
    public MappingTuple consumeSource() {
        MappingTuple tuple = new MappingTuple();
        try {
            this.init();
            while (this.sourceIterator.hasNext()) {
                queueNextSolutionMappings();
                // put queue contents in tuple, and clear
                while (!solutionMappingQueue.isEmpty()) {
                    tuple.addSolutionMap(this.defaultFragment, solutionMappingQueue.poll());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return tuple;
    }

    // Multiple solutions are queued if there is lists usage (e.g. authors[*]) -> {book1, author:author1}, {book1, author, author2}
    private void queueNextSolutionMappings() {
        JSONRecord r = (JSONRecord) sourceIterator.next();
        JSONObject json = new JSONObject((Map<String, ?>) r.get("$").getValue());
        List<SolutionMapping> maps = parseFields(json, this.fields);
        maps.forEach(m -> m.put("#", new LiteralNode(r.getIndex(), XSDDatatype.XSDinteger)));

        solutionMappingQueue.addAll(maps);
    }

    private List<SolutionMapping> parseFields(JSONObject json, List<Field> fields) {
        List<SolutionMapping> sms = new ArrayList<>();
        for (Field f : fields) {
            List<SolutionMapping> output = new ArrayList<>();
            if (f instanceof ExpressionField expField) {
                output.add(parseExpressionField(json, expField));
            } else if (f instanceof IterableField iterableField) {
                output.addAll(parseIterableField(json, iterableField));
            } else {
                throw new RuntimeException("Unrecognized field type: " + f.getClass().getName());
            }

            if (sms.isEmpty()) {
                sms.addAll(output);
            } else if (sms.size() < output.size()) {
                sms = new ArrayList<>(
                        sms.stream()
                                .flatMap(o -> output.stream().map(o::union))
                                .toList());
            } else if (sms.size() == output.size()) {
                for (int i = 0; i < sms.size(); i++) {
                    sms.set(i, sms.get(i).union(output.get(i)));
                }
            }
        }

        return sms;
    }

    private List<SolutionMapping> parseIterableField(JSONObject json, IterableField field) {
        Object subObject = JsonPath.read(json, field.iterator());
        List<SolutionMapping> sms = new ArrayList<>();

        if (subObject instanceof ArrayList<?> objArray) {
            for (Object o : objArray) {
                Map<String, Object> obj = (Map<String, Object>) o;
                parseFields(new JSONObject(obj), field.getSubfields())
                        .stream().reduce(SolutionMapping::union)
                        .ifPresent(sms::add);
            }
        } else { // object
            sms.addAll(parseFields(new JSONObject((Map<String, Object>) subObject), field.getSubfields()));
        }

        for (SolutionMapping sm : sms) {
            for (String key : new HashSet<>(sm.keySet())) {
                if (!key.endsWith(".#"))
                    sm.put(key + ".#", new LiteralNode(0, XSDDatatype.XSDinteger));
            }
        }


        // prepend the fields with the alias of the iterable field
        List<SolutionMapping> sms2 = sms.stream().map(sm -> {
            SolutionMapping smNew = new SolutionMapping();
            for (var e : sm.entrySet()) {
                smNew.put(field.name() + "." + e.getKey(), e.getValue());
            }

            return smNew;
        }).toList();

        for (int i = 0; i < sms2.size(); i++) {
            SolutionMapping sm = sms2.get(i);
            sm.put(field.name() + ".#", new LiteralNode(i, XSDDatatype.XSDinteger));
        }

        return sms2;
    }

    private SolutionMapping parseExpressionField(JSONObject json, ExpressionField expField) {
        if (expField.constant() != null) {
            return new SolutionMapping(Map.of(expField.name(), expField.constant()));
        }

        Object value = JsonPath.read(json, expField.iterator());

        return new SolutionMapping(Map.of(
                expField.name(), new LiteralNode(value),
                expField.name() + ".#", new LiteralNode(0, XSDDatatype.XSDinteger)
        ));
    }

    @Override
    @NonNull
    protected MappingTuple nextEffective() {
        if (solutionMappingQueue.isEmpty() && this.sourceIterator.hasNext()) {
            queueNextSolutionMappings();
        }
        MappingTuple tuple = new MappingTuple();
        SolutionMapping map = solutionMappingQueue.poll();
        tuple.addSolutionMap(this.defaultFragment, map);
        return tuple;

    }

    @Override
    public boolean hasNext() {
        // No need to check if source iterator is null, since this would throw an exception during the init.
        return this.isReady() && (!this.solutionMappingQueue.isEmpty() || (this.sourceIterator.hasNext()));
    }

    @Override
    public void init() {
        try {
            this.sourceIterator = new JSONSourceIterator(this.access, this.rootIterator);
            this.setReady(true);
        } catch (Exception e) {
            this.setReady(false);
            throw new RuntimeException(e);
        }
    }
}
