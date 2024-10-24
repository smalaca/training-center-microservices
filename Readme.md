# Training Center
## Goal
The purpose of this repository is to show how to build and maintain the project using following solutions:
- microservices 
- event storming 
- port and adapters architecture
- domain-driven design

## Table of Content
Below you can find a link to articles that explains used tools and techniques, how the project was developed, and how the decision were made:
1. [Technology](#technology)
2. [Artefacts](#artefacts)
3. [Business Overview](#business-overview)
   1. [Brief description](#brief-description)
   2. [High-level requirements](#high-level-requirements)
4. [Education](#education)
   1. [Articles](#articles)
   2. [Books worth reading](#books-worth-reading)
   2. [Resources](#resources)

## Technology
- Docker
- Kafka
- Java 17
- Spring Boot

## Artefacts
- [Miro Board](https://miro.com/app/board/uXjVKVYAGuE=/?share_link_id=139271017254) covers:
  - Big Picture Event Storming
  - Bounded Contexts
  - Core Domains, Supporting and Generic subdomains
  - Process Level Event Storming

## Business Overview
### Brief description
Institution that provides structured training and education in various fields.

### High-level requirements
#### Trainings
Trainings ready to be conducted and those under preparation.
1. Viewing trainings by those who are interested in participation.
2. Adding new and modifying existing trainings.
3. Each training needs to have its supervisors.

#### Advertisement
Activities made to attract potential attendees to participate in a training:
1. Webinars.
2. Talks on Conferences.
3. Talks on internal events.

#### Trainers
Trainers the Training Center is working with.
1. What trainings trainer can conduct.
2. What skills a trainer has.
3. Trainers' rates.
4. History of conducted trainings with received feedback.
5. Agreement between Trainer and the Training Center.

#### Open trainings
Trainings anyone can join.
1. Trainings' harmonogram.
2. Payment.
3. Resignation.
4. Agreement with the Trainer who should conduct the training.
5. Group limits.
6. Cancellation.

#### Dedicated trainings
Trainings organized for employees of other companies:
1. Training adjustment and modifications.
2. Conversations with company's representative.
3. Non-Disclosure Agreement.

## Education
### Articles
List of the articles that explains how the project evolved and explains how decisions were made:
1. **Introduction**
   1. [Rise Above! Elevating Your Code Craftsmanship](https://letstalkaboutjava.blogspot.com/2024/04/rise-above-elevating-your-code.html)
   2. [Elevating Your Code Craftsmanship: Questions and Answers](https://letstalkaboutjava.blogspot.com/2024/05/elevating-your-code-craftsmanship.html)
2. **Event Storming Introduction**
   1. [The Power of Post-Its: Deciphering Business Processes with Event Storming](https://letstalkaboutjava.blogspot.com/2024/05/the-power-of-post-its-deciphering.html)
   2. [Setting the Stage: Preparing for Your Event Storming Workshop](https://letstalkaboutjava.blogspot.com/2024/05/setting-stage-preparing-for-your-event.html)
   3. [Event Storming Workshops: A Closer Look at Different Approaches](https://letstalkaboutjava.blogspot.com/2024/05/event-storming-workshops-closer-look-at.html)
   4. [Event Storming Workshops: Tips and Tricks](https://letstalkaboutjava.blogspot.com/2024/06/event-storming-workshops-tips-and-tricks.html)
3. **Big Picture Event Storming**
   1. [Navigating the Storm: A Guide to Big Picture Event Storming](https://letstalkaboutjava.blogspot.com/2024/06/navigating-storm-guide-to-big-picture.html)
   2. [Taming the Storm: How Chaotic Exploration Shapes Big Picture Event Storming](https://letstalkaboutjava.blogspot.com/2024/06/taming-storm-how-chaotic-exploration.html)

### Books worth reading
- **Domain-Driven Design**
  - [Domain-Driven Design Distilled](https://www.oreilly.com/library/view/domain-driven-design-distilled/9780134434964/) by Vaughn Vernon
  - [Implementing Domain-Driven Design](https://www.oreilly.com/library/view/implementing-domain-driven-design/9780133039900/) by Vaughn Vernon
  - [Learning Domain-Driven Design](https://www.oreilly.com/library/view/learning-domain-driven-design/9781098100124/) by Vlad Khononov
- **Event Storming**
  - [Introducing EventStorming](https://www.eventstorming.com/book/) by Alberto Brandolini
- **Microservices**
  - [Building Microservices](https://www.oreilly.com/library/view/building-microservices-2nd/9781492034018/) by Sam Newman
  - [Microservices Patterns](https://www.oreilly.com/library/view/microservices-patterns/9781617294549/) by Chris Richardson
  - [Monolith to Microservices](https://www.oreilly.com/library/view/monolith-to-microservices/9781492047834/) by Sam Newman
  - [Production-Ready Microservices](https://www.oreilly.com/library/view/production-ready-microservices/9781491965962/) by Susan J. Fowler
  - [Strategic Monoliths and Microservices: Driving Innovation Using Purposeful Architecture](https://www.oreilly.com/library/view/strategic-monoliths-and/9780137355600/) by Vaughn Vernon, Tomasz Jaskula
- **Software Architecture**
  - [Balancing Coupling in Software Design](https://www.oreilly.com/library/view/balancing-coupling-in/9780137353514/) by Vlad Khononov
  - [Building Evolutionary Architectures](https://www.oreilly.com/library/view/building-evolutionary-architectures/9781492097532/) by Neal Ford, Rebecca Parsons, Patrick Kua, Pramod Sadalage
  - [Clean Architecture](https://www.oreilly.com/library/view/clean-architecture-a/9780134494272/) by Robert C. Martin
  - [Designing Data-Intensive Applications](https://www.oreilly.com/library/view/designing-data-intensive-applications/9781491903063/) by Martin Kleppmann
  - [Event Processing. Designing IT Systems for Agile Companies](https://www.oreilly.com/library/view/event-processing-designing/9780071633505/) by K. Chandy, W. Roy Schulte
- **Software Development**
  - [Release It!](https://www.oreilly.com/library/view/release-it/9781680500264/) by Michael T. Nygard
  - [The Phoenix Project](https://www.oreilly.com/library/view/the-phoenix-project/9781457191350/) by Gene Kim, Kevin Behr, George Spafford
- **Testing**
   - [Developer Testing: Building Quality into Software](https://www.oreilly.com/library/view/developer-testing-building/9780134291109/) by Alexander Tarlinder

### Resources
- **Architecture Decision Record**
  - [Architecture decision record](https://github.com/joelparkerhenderson/architecture-decision-record) (ADR) by Joel Parker Henderson
- **Event Storming**
  - [Awesome EventStorming](https://github.com/mariuszgil/awesome-eventstorming) by Mariusz Gil
  - [Introducing Event Storming](https://ziobrando.blogspot.com/2013/11/introducing-event-storming.html) by Alberto Brandolini
- **Testing**
  - [ArchUnit](https://www.archunit.org/)