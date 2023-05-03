
# TODO
- Implement Dependency injection
- - Ensure changeUnit order


# Technical debts
- Implement transaction wrapper
- Implement Lock
- When a non-transactional change fails, the rollback override the failed execution in DB. 
  It should contain both, so we have the history
- Test/debug mongodb dates

