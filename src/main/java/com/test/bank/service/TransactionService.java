package com.test.bank.service;

import com.test.bank.db.tables.records.UserRecord;
import com.test.bank.initializer.DataSourceInitializer;
import com.test.bank.model.TransferResponse;
import com.test.bank.model.User;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.types.UInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.test.bank.db.Tables.USER;

@Singleton
public class TransactionService {

    DefaultConfiguration jooqConfiguration;

    @Inject
    public TransactionService(DataSourceInitializer dataSourceInitializer) {
        this.jooqConfiguration = dataSourceInitializer.getJooqConfiguration();
    }

    public TransferResponse transfer(int fromUserId, int toUserId, int amount) {
        // TODO implement transfer
        UserRecord fromUserRecord = DSL.using(jooqConfiguration).fetchOne(USER, USER.ID.eq(UInteger.valueOf(fromUserId)));
        UserRecord toUserRecord = DSL.using(jooqConfiguration).fetchOne(USER, USER.ID.eq(UInteger.valueOf(toUserId)));
        TransferResponse res = null;
        if(fromUserRecord == null || toUserRecord == null){
            return res;
        }

        User fromUser = fromUserRecord.into(User.class);
        User toUser = toUserRecord.into(User.class);

        if(fromUser.getWallet() >= amount) {
            fromUser.setWallet(fromUser.getWallet() - amount);
            toUser.setWallet(toUser.getWallet() + amount);

            DSL.using(jooqConfiguration).update(USER)
                .set(USER.WALLET, fromUser.getWallet())
                .where(USER.ID.eq(UInteger.valueOf(fromUserId)))
                .execute();
        
            DSL.using(jooqConfiguration).update(USER)
                .set(USER.WALLET, toUser.getWallet())
                .where(USER.ID.eq(UInteger.valueOf(toUserId)))
                .execute();

            res = new TransferResponse();
            res.setFromUser(fromUser);
            res.setToUser(toUser);
        }
        return res;
    }

}
