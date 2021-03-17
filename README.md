# Griffin programming test

# Transactions that violate balance constraints (API + Postgres)

There are two steps to applying a transaction:

- First, we check if applying the transaction would violate a balance constraint, i.e. you want to debit £10 but the balance is only £5 on a bank account
- If the above check passes (i.e. no balance constraints are violated), we apply the transaction
 
Since there are two steps, and we are getting multiple requests coming into the app concurrently, the situation could arise where, for a single request:

- We find that there are no balance constraints for an account (e.g. a balance £100 and a transaction debiting £50), so continue processing the transaction
- Before actually applying the transaction, but after the above check, another transaction is applied (e.g. a balance £100 and a transaction debiting £80, making the new balance £20)
- We apply the transaction debiting £50 to the account, but the balance of the account is £20, resulting in a balance of -£30, violating balance constraints

The above problem could happen if are only validating the balance constraints in the "middelware" portion of the app, and once we get past this as described above, there is nothing stopping the invalid transaction from happening.
In this case, the problem could be mitigated by using an unsigned int in the database, and having another column decide whether the account is in the positive (for a bank account) or negative (for a loan).

Another problem could arise at the database if the two steps - verifying and then applying the bank transaction - are not wrapped in a _database transaction_. As described above for the API, the verification step could pass, and more writes could be made to the database in the mean time, changing the balance to a value which would violate the balance constraint if our bank transaction is applied. By wrapping the two operations in a database transaction and locking the row for that account while the database transaction takes place, we ensure that the account is not altered while we are applying a bank transaction. Since we are locking an account every time we get and set its balance, this does incur a performance cost, as every concurrent request on a given account must wait until the lock is released, or in other words "get in line" for the request to be fulfilled.

# Incorrect balances (kafka + rocksDB)

- System goes down before writing KV state to topic, but after applying new account balances. When we bring the stream back up, the KV state will be behind the actual account state (missing a KV write for the transaction that was applied)
