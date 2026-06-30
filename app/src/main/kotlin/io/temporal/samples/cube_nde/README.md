# cube_nde

## 1. Create the search attributes

```bash
temporal operator search-attribute create --name ClaimToken                    --type Keyword
temporal operator search-attribute create --name TransactionType               --type Keyword
temporal operator search-attribute create --name MostRecentCompletedActivity   --type Keyword
temporal operator search-attribute create --name WorkflowVersion               --type Int
temporal operator search-attribute create --name MostRecentFailedActivity      --type Keyword
temporal operator search-attribute create --name ClaimType                     --type Keyword
temporal operator search-attribute create --name CustomerToken                 --type Keyword
temporal operator search-attribute create --name WorkflowVersions              --type KeywordList
temporal operator search-attribute create --name DisputedTransactionToken      --type Keyword
temporal operator search-attribute create --name MostRecentStartedActivity     --type Keyword
```

## 2. Start the worker

```bash
./gradlew :app:run -PmainClass=io.temporal.samples.cube_nde.AppKt --args="--mode worker"
```

## 3. Start the starter

```bash
./gradlew :app:run -PmainClass=io.temporal.samples.cube_nde.AppKt --args="--mode starter"
```

## Switch SDK versions

The Temporal Java SDK version is pinned in `gradle.properties` at the repo
root via the `temporalVersion` property. The investigation toggles between:

```properties
temporalVersion=1.35.0
#temporalVersion=1.36.0
```

To reproduce the cross-version replay path: run the workflow on 1.35.0,
stop the worker, flip the property to 1.36.0, then restart the worker so
it picks the in-flight workflow back up from a cold cache.
