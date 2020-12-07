package com.kevinarpe.suruga_bank.db;

import org.jooq.Query;
import org.jooq.Record;
import org.jooq.ResultQuery;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public interface DSLContextService {

    int execute(Query query)
    throws Exception;

    <TRecord extends Record>
    TRecord selectExactlyOne(ResultQuery<TRecord> query)
    throws Exception;
}
