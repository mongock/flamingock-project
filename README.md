
# TODO
- Implement proxy wrapper

# Technical debts
- Ensure changeUnit order in ChangeUnits
- Implement transaction wrapper
- Implement Lock
- When a non-transactional change fails, the rollback override the failed execution in DB. 
  It should contain both, so we have the history
- Test/debug mongodb dates
- See potential similarities between `ReflectionUtil` and `RuntimeHelper`

