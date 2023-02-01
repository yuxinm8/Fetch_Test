# Fetch_Test
## Run program
```
javac Balance.java
java Balance 5000
```

## Assumption
- Points in transaction.cvs is integer.
- The data in transaction.cvs is valid.
- The sum of points of each payer is non-negative and is not exceed 2^31-1.
- If two transactions happens at the same time, the points of payer whose name is lexicographically smaller is viewed as older points.
