
# TODO
- Sort changeUnits
- Lock

# Technical debts
- Remove RuntimeManager.getDependencyManager() and pass the DependencyManager to navigator
- Provide support for dependency injection per task
- Ensure changeUnit order in ChangeUnits
- Implement Lock
- When a non-transactional change fails, the rollback override the failed execution in DB. 
  It should contain both, so we have the history
- Test/debug mongodb dates
- See potential similarities between `ReflectionUtil` and `RuntimeHelper`
- Ideally, even in failed scenario, the Summary shows all the tasks, including the ones that wasn't executed because a
  previous one failed.
- Summary(specially the one returned in the event) should have the list of executed ids
