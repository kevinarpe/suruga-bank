package com.kevinarpe.suruga_bank.db;

import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import com.googlecode.kevinarpe.papaya.exception.ExceptionThrower;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultQuery;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
// Scope: Global singleton
public final class DSLContextServiceImp
implements DSLContextService {

    private final ExceptionThrower exceptionThrower;

    public DSLContextServiceImp(ExceptionThrower exceptionThrower) {

        this.exceptionThrower = ObjectArgs.checkNotNull(exceptionThrower, "exceptionThrower");
    }

    @Override
    public int
    execute(Query query)
    throws Exception {
        try {
            final int x = query.execute();
            return x;
        }
        catch (Exception e) {
            throw exceptionThrower.throwChainedCheckedException(Exception.class,
                e,
                "Failed to execute query:%n%s", query);
        }
    }

    @Override
    public <TRecord extends Record>
    TRecord
    selectExactlyOne(ResultQuery<TRecord> query)
    throws Exception {

        final Result<TRecord> result = _selectMany(query);
        final int size = result.size();
        switch (size) {
            case 0:
                throw exceptionThrower.throwCheckedException(Exception.class,
                    "Query selected zero rows:%n%s", query);
            case 1: {
                final TRecord x = result.get(0);
                return x;
            }
            default: {
                throw exceptionThrower.throwCheckedException(Exception.class,
                    "Expected exactly one row, but query selected %d rows:%n%s", size, query);
            }
        }
    }

    private <TRecord extends Record>
    Result<TRecord>
    _selectMany(ResultQuery<TRecord> query)
    throws Exception {
        try {
            final Result<TRecord> x = query.fetch();
            return x;
        }
        catch (Exception e) {
            throw exceptionThrower.throwChainedCheckedException(Exception.class,
                e,
                "Failed to execute query:%n%s", query);
        }
    }
}
