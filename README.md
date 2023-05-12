
# TODO
- Inject ConnectionEngine's dependencies to runtimeHelper
  - Divide dependencies inside the DependencyManager between standard and "priority"/"isolated"/"specific"
  - To what component the dependency should be injected to? Runtime(delegating to DependencyManager), DependencyManager directly?
  - Inject DependencyContext/DependencyInjector(current DependencyManager) and proxy and generate RuntimeHelper?.
- Implement Mongock transaction wrapper
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
