# jsonql-core

This library is a collection of DTO frames that can be used to build data filters with JSON/GraphQL interface. You can think about it as a simple query language, like SQL, but done with JSON and supporting pagination and sorting. Using this lib you can easily build the following example queries:

1. Give me a list of all users.
1. Give me a list of all users sorted by `name`.
1. Give me a first page of all users with `pagesize = 10`.
1. Give me a list of users which status is `ACTIVE`.
1. Give me a list of users which status is `ACTIVE` or `BANNED`.
1. Give me a list of users which are admins.
1. Give me a first page of all users sorted by `name`, which are admins with `ACTIVE` or `BANNED` status.  

... etc.

Besides uniform filtering frames, we also provide [here](https://github.com/json-ql) data fetchers working with various sources:

- SQL databases:
  - [JPA](https://github.com/json-ql/jsonql-jpa), which can be based on Hibernate or other JPA-compatible technology.
- NOSQL databases:
  - [MongoDB](https://github.com/json-ql/jsonql-mongo).
- Full-text indexes:
  - [Hibernate Search with Lucene](https://github.com/json-ql/jsonql-hibernate-search) backend.
  - [Hibernate Search with ElasticSearch](https://github.com/json-ql/jsonql-hibernate-search-elastic) backend. 

## How to use

This lib uses [GitHub packages feature](https://github.com/features/packages), however it's currently a kind of broken feature for open source projects, because it [requires to authenticate](https://github.community/t5/GitHub-API-Development-and/Download-from-Github-Package-Registry-without-authentication/m-p/35501#M3312) even to fetch public packages. Currently to add this library dependency to a project you need to:

1. Generate a [Personal Access Token](https://github.com/settings/tokens) with **read:packages** permission.
1. Use following dependency (`gradle.build` example):

```groovy
plugins {
    id 'java'
}

repositories {
	maven { 
		url = "https://maven.pkg.github.com/json-ql/jsonql-core" 
		credentials {
			username = 'nobody'
			password = 'USE_TOKEN_HERE'
		}
	}
}

dependencies {
    implementation 'com.lifeinide.jsonql:jsonql-core:VERSION'
}
```

Check [here](https://github.com/orgs/json-ql/packages) for available package versions.  

## Default filters provided

Following default filter frames are provided by this lib.

### [`SingleValueQueryFilter`](src/main/java/com/lifeinide/jsonql/core/filters/SingleValueQueryFilter.java)

Filters any single value with one of predefined [conditions](src/main/java/com/lifeinide/jsonql/core/enums/QueryCondition.java):

```json
{
  "value": "a"
}

{
  "condition": "eq",
  "value": "a"
}

{
  "condition": "gt",
  "value": 10
}

{
  "condition": "notNull"
}
``` 

### [`EntityQueryFilter`](src/main/java/com/lifeinide/jsonql/core/filters/EntityQueryFilter.java)

Special kind of single value `QueryFilter` assumed to be working with entity ID.

```json
{
  "value": "b9a103d6-a9dd-4371-9d2b-1b008bf88327"
}
``` 

### [`DateRangeQueryFilter`](src/main/java/com/lifeinide/jsonql/core/filters/DateRangeQueryFilter.java) 

Filters date by from-to range.

```json
{
  "from": "2018-01-01",
  "to": "2018-03-03"
}

{
  "range": "LAST_30_DAYS"
}

{
  "range": "PREVIOUS_MONTH"
}
``` 

### [`ValueRangeQueryFilter`](src/main/java/com/lifeinide/jsonql/core/filters/ValueRangeQueryFilter.java).

Filters numeric value by from-to range.

```json
{
  "from": 10,
  "to": 20
}
``` 

### [`ListQueryFilter`](src/main/java/com/lifeinide/jsonql/core/filters/ListQueryFilter.java)

Combines multiple filters with and/or conjunction.

```json
{
  "filters": [
    {
      "value": "a"
    },
    {
      "value": "b"
    }
  ]
}

{
  "conjunction": "and",
  "filters": [
    {
      "condition": "gt",
      "value": 10
    },
    {
      "conditions": "ne",
      "value": 100
    }
  ]
}
```

## Query builder abstraction

A query builder is an abstraction used to fetch paginated data from the persistence storage depending on the incoming request comprised of the combination of filters. Before you get the data, you need to implement you own frame with custom filters:

```java
class UserFilter extends DefaultPageableRequest {

	protected SingleValueQueryFilter<String> name;
	protected SingleValueQueryFilter<Boolean> admin;
  
	public SingleValueQueryFilter<String> getName() {
		return name;
	}

	public void setName(SingleValueQueryFilter<String> name) {
		this.name = name;
	}

	public SingleValueQueryFilter<Boolean> getAdmin() {
		return admin;
	}

	public void setAdmin(SingleValueQueryFilter<Boolean> admin) {
		this.admin = admin;
	}
	
}
```

`DefaultPageableRequest` supports pagination and sorting out of the box, therefore we can now query for list of users using following example request:

```json
{
  "admin": {
    "value": "true"
  },
  "name": {
    "condition": "notNull"
  },
  "pageSize": 20,
  "page": 3,
  "sort": [{
  	"sortDirection": "asc",
  	"sortField": "name"
  }]
}
```

To fetch the data from the data source you need to have a working implementation of `BaseFilterQueryBuilder`, for example:

```java
public class JpaFilterQueryBuilder<E> extends BaseFilterQueryBuilder<...> {

    // the custom query builder implementation

}
```

Now, in your controller you can simply get the data in the following way:

```java
public Page<User> listUsers(UserFilter req) {
	new JpaFilterQueryBuilder<User>(...)
		.add("name", req.getName())
		.add("admin", req.isAdmin())
		.list(req);
}
```

## How to implement a custom filter

To implement custom filter implement [`QueryFilter`](src/main/java/com/lifeinide/jsonql/core/intr/QueryFilter.java) interface in your custom filtering frame. Each `FilterQueryBuilder` implementation contains following not implemented method:

```java
@Override
public SELF add(String field, QueryFilter filter) {
	throw new IllegalStateException(String.format("Support for filter: %s in builder: %s is not implemented",
		filter.getClass().getSimpleName(), getClass().getSimpleName()));
}
```

You can just extend `FilterQueryBuilder` implementation and implement this method with your custom filters support. 

In case you want to delegate custom query builder execution to some external class, you can always pass to it `FilterQueryBuilder` instance which implements `context()` method that can be used to join to current query building process.

## Project supporters

Thanks for supporting this project to:
 
- [Two Fish Software](https://twofishsoftware.com/)
- [Depoway](http://depoway.com)
