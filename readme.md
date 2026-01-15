| Package     | Role            | Key                                                                                       | Responsibility             |
|-------------|-----------------|-------------------------------------------------------------------------------------------|----------------------------|
| routes      | Entry Point     | "Maps URLs to Controller methods (e.g.                                                    | /login → AuthController)." |
| controllers | Request Handler | Orchestrates the request; calls Services and returns HTTP responses.                      |                            |
| services    | Business Logic  | "The ""Brain."" Handles permissions                                                       | calculations               |  and complex workflows."
| validation  | Guardrail       | "Checks if data is valid (e.g. ""Is the email formatted correctly?"") before processing." |                            |
| data        | Entities        | "Java objects that mirror your database tables (e.g.                                      | User                       |  Blog)."
| dao         | Database Access | Executes raw SQL or JPA queries. The only place that touches the DB.                      |                            |
| util        | Helpers         | "Shared tools like database connection strings                                            | logging                    |  and constants."
| dto         | Data Transfer   | Stripped-down versions of objects for the frontend (hides sensitive data).                |                            |
| exception   | Error Handling  | Defines custom errors like UserNotFoundException for cleaner debugging.                   |                            |