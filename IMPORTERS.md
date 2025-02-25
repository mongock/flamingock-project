# Importers Analysis

## Types
### MongoDB
| Name                      | Source  | Destination | Database |
|:--------------------------|:-------:|:-----------:|:--------:|
| cloud-from-legacy-mongodb | Mongock |    Cloud    | MongoDB  |
| cloud-from-local-mongodb  | Mongock |    Cloud    | MongoDB  |
| local-from-legacy-mongodb |  Local  |    Local    | MongoDB  |

### DynamoDB
| Name                       |  Source  | Destination | Database |
|:---------------------------|:--------:|:-----------:|:--------:|
| cloud-from-legacy-dynamodb | Mongock  |    Cloud    | DynamoDB |
| cloud-from-local-dynamodb  | Mongock  |    Cloud    | DynamoDB |
| local-from-legacy-dynamodb |  Local   |    Local    | DynamoDB |

### From legacy
- requires read from database
- To write we only need an AuditWriter

## Notes



# Flamingock improvements
### Client
- **[Importer]** If flamingock detects legacy `@ChangeUnit` annotation for changeLogs not appearing in as executed or failed and there is no importer changeUnit executed,  it should ask for importer 
  - Conditions to be right
    - NO legacy `@ChangeUnits`
    - Legacy `@ChangeUnits` + importer changUnit has been executed successfully
    - Legacy `@ChangeUnits` + all the legacy changeUnits appear as executed
    - Legacy `@ChangeUnits` + importer
- We are not sending a good errorTrace

### Server
- More logs
- Import service to receive Audit Entry
- Extend audit service to receive a list of audit entries
- Use audit service instead of Import Service(Think about this!)

### Comment to guys
- DoD -> Ensure end to end validation.

