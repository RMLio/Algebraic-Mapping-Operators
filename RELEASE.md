# Releasing Algebraic Mapping Operators

Step-by-step instructions for publishing a release. [HANDBOOK.md](HANDBOOK.md) (Release process) explains how the tooling works.

## Before you start

- bash (Git Bash on Windows), Maven, Java 21, and `changefrog` (`npm install -g changefrog`), which writes the version section of `CHANGELOG.md`.
- Push access to `origin` (https://gitlab.ilabt.imec.be/rml/proc/algebraic-mapping-operators).
- You are on `development`, up to date with `origin/development`, with a clean working tree.
- The tests pass: `mvn verify`.
- `## Unreleased` in `CHANGELOG.md` lists everything since the last release.
- Pick the version `<version>`, in the format `X.Y.Z`, with [Semantic Versioning](https://semver.org/). `pom.xml` holds the planned version as `<version>-SNAPSHOT`: release that version, or a higher one when the changelog has new features or breaking changes.

## Release

1. Run `./bump-version.sh <version>` and answer `y` to both questions. The script
   - sets the version in `pom.xml` and the version in `README.md`;
   - turns `## Unreleased` into the version section of `CHANGELOG.md`;
   - commits "Update version to <version>", pushes `development`, and creates and pushes the tag `v<version>`;
   - moves `pom.xml` to the next patch `-SNAPSHOT`, and commits and pushes "Prepare for next development cycle".
2. Move `main` to the release: `git push origin v<version>^{commit}:main`. `main` always points at the latest release; the push succeeds only as a fast-forward.
3. The tag pipeline (https://gitlab.ilabt.imec.be/rml/proc/algebraic-mapping-operators/-/pipelines) builds the release with the `release` Maven profile, signs it and deploys it to Maven Central. Check that its deploy job succeeds; the new version then appears at https://repo1.maven.org/maven2/be/ugent/idlab/knows/algebraic-mapping-operators/ (this can take up to an hour).

## After the release

- GitLab mirrors the branches and tags to GitHub (https://github.com/RMLio/Algebraic-Mapping-Operators); check that the tag is there.
- Update the consumers: MappingWeaver-java: set the default of `amo.version` to the new release.

## When something goes wrong

- The script stops at the first failing command. When it stops before pushing, fix the cause, discard its changes (`git reset --hard origin/development`) and run it again. When it stops after pushing `development`, finish the remaining steps by hand: create the tag `v<version>` on the version commit if it is missing, push it (`git push origin v<version>`), then set the next `-SNAPSHOT` (`mvn versions:set -DnewVersion=<next>-SNAPSHOT -DgenerateBackupPoms=false`), commit "Prepare for next development cycle" and push.
- Once the tag is pushed, keep it: fix the cause and retry the failed pipeline job. When the released code itself is broken, release the next patch version.
