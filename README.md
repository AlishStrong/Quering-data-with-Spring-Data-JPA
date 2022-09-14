# Querying data with Spring Data JPA

## Session structure

Welcome, in this session you will learn how to query data using `Spring Data JPA`.
The session is divided into 3 parts:
1. Using JPA repositories - branch name `part-1-using-jpa-repos`;
2. Derived queries - branch name `part-2-derived-queries`;
3. Query annotation - branch name `part-3-query-annotation`;

Each part (and its git branch) has a corresponding **complete** branch: `part-1-complete`, `part-2-complete`, `part-3-complete`.

Go through parts step-by-step by executing **git checkout** command, e.g. 
```bash
git checkout -b part-1-using-jpa-repos origin/part-1-using-jpa-repos
``` 

## System requirements
Make sure that your machine has the following things installed:
- **Java 8**
- **Maven 3.7**
- **Docker**
- **Docker Compose** (though later versions of **Docker** include CLI commands for compose as well)

It is also recommended to have the following (though not crucial):
- **DBMS** application, e.g. **MySQL Workbench** or **DBeaver**
- **IDE** application, e.g. **Visual Studio Code**, **IntelliJ**, **Eclipse**

## Starting the project
1. Start the `mysql-db` service in **Docker container**:

    1.1. Navigate to the root directory of the project in a terminal

    1.2. Execute the following command:
    ```bash
    docker-compose up -d
    ```

      OR if you use the latest version of **Docker**

    ```bash
    docker compose up -d
    ```
2. Start the `backend` application:

    2.1. Navigate to the **backend** directory of the project in a terminal

    2.2. Start the project via **IDE** that you have.
    If you don't have **IDE**, then you can execute the following **Maven** command in the directory via a terminal:
    ```bash
    mvn spring-boot:run
    ```
