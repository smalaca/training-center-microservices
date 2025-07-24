# Mermaid Diagram Rendering Test

This file is for testing that Mermaid diagrams render correctly in your Markdown environment.

## Test Diagrams

### Simple Flowchart

```mermaid
flowchart LR
    A[Start] --> B{Decision}
    B -->|Yes| C[Do Something]
    B -->|No| D[Do Something Else]
    C --> E[End]
    D --> E
```

### CQRS Pattern (from ADR 0017)

```mermaid
flowchart TB
    subgraph "Command Side"
        TP[Training Program Module]
        TO[Training Offer Module]
        TP_DB[(Training Program DB)]
        TO_DB[(Training Offer DB)]
        TP --> TP_DB
        TO --> TO_DB
    end
    
    subgraph "Events"
        EV[Event Bus / Kafka]
    end
    
    subgraph "Query Side"
        TC[Training Catalogue Module]
        TC_DB[(Read Model DB)]
        TC --> TC_DB
    end
    
    TP --> EV
    TO --> EV
    EV --> TC
    
    Client1[Client] --> TP
    Client2[Client] --> TO
    Client3[Client] --> TC
    
    classDef command fill:#f96,stroke:#333,stroke-width:2px
    classDef query fill:#9cf,stroke:#333,stroke-width:2px
    classDef event fill:#fc9,stroke:#333,stroke-width:2px
    
    class TP,TO,TP_DB,TO_DB command
    class TC,TC_DB query
    class EV event
```

### Layered Architecture (from ADR 0016)

```mermaid
flowchart TB
    subgraph "Training Catalogue Module"
        subgraph "Presentation Layer"
            REST[REST API Controller]
            KAFKA[Kafka Listener]
        end
        
        subgraph "Service Layer"
            SVC[Service Components]
        end
        
        subgraph "Data Access Layer"
            REPO[Repositories]
            DB[(Database)]
        end
        
        %% Layer connections
        REST --> SVC
        KAFKA --> SVC
        SVC --> REPO
        REPO --> DB
        
        %% External connections
        Client[Client] --> REST
        Events[Event Bus / Kafka] --> KAFKA
    end
    
    classDef presentation fill:#f9a,stroke:#333,stroke-width:2px
    classDef service fill:#adf,stroke:#333,stroke-width:2px
    classDef data fill:#ad9,stroke:#333,stroke-width:2px
    classDef external fill:#ddd,stroke:#333,stroke-width:1px
    
    class REST,KAFKA presentation
    class SVC service
    class REPO,DB data
    class Client,Events external
```

### Port and Adapters with DDD (from ADR 0015)

```mermaid
flowchart TB
    subgraph "Reviews Service"
        subgraph "Domain Layer"
            AGG[Aggregates]
            VO[Value Objects]
            DOM_SVC[Domain Services]
            
            AGG --- VO
            AGG --- DOM_SVC
        end
        
        subgraph "Application Layer"
            APP_SVC[Application Services]
            PORTS_OUT[Output Ports]
            PORTS_IN[Input Ports]
            
            APP_SVC --> PORTS_OUT
            PORTS_IN --> APP_SVC
            APP_SVC --> AGG
        end
        
        subgraph "Infrastructure Layer"
            REST_ADPT[REST Adapter]
            KAFKA_ADPT[Kafka Adapter]
            REPO_ADPT[Repository Adapter]
            DB[(Database)]
            
            REST_ADPT --> PORTS_IN
            KAFKA_ADPT --> PORTS_IN
            PORTS_OUT --> REPO_ADPT
            REPO_ADPT --> DB
        end
    end
    
    Client[Client] --> REST_ADPT
    Events[Event Bus / Kafka] --> KAFKA_ADPT
    
    classDef domain fill:#f9d,stroke:#333,stroke-width:2px
    classDef application fill:#adf,stroke:#333,stroke-width:2px
    classDef infrastructure fill:#ad9,stroke:#333,stroke-width:2px
    classDef external fill:#ddd,stroke:#333,stroke-width:1px
    
    class AGG,VO,DOM_SVC domain
    class APP_SVC,PORTS_OUT,PORTS_IN application
    class REST_ADPT,KAFKA_ADPT,REPO_ADPT,DB infrastructure
    class Client,Events external
```

## How to Test Rendering

To verify that the diagrams render correctly:

1. **GitHub**: Push this file to a GitHub repository and view it on GitHub.com.

2. **GitLab**: Push this file to a GitLab repository and view it on GitLab.com.

3. **IDE Preview**: Use the Markdown preview feature in your IDE (e.g., IntelliJ IDEA, VS Code).

4. **Mermaid Live Editor**: Copy the Mermaid code blocks to [Mermaid Live Editor](https://mermaid.live/) to verify syntax.

If the diagrams don't render correctly in your preferred Markdown environment, you may need to:

- Check if Mermaid is supported in that environment
- Verify the Mermaid syntax is correct
- Consider using a different Markdown environment that supports Mermaid

## Troubleshooting

If diagrams don't render:

1. Ensure there are no syntax errors in the Mermaid code.
2. Check that the triple backticks and `mermaid` language specifier are correctly formatted.
3. Verify that your Markdown viewer supports Mermaid diagrams.
4. Try viewing the file in a different environment (e.g., GitHub, GitLab, VS Code).

For GitHub repositories, you may need to enable Mermaid support in your repository settings.