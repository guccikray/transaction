import service.TransactionService;
import transaction.Transaction;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import static service.TransactionService.KEY_PAIR_SIZE;

public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        TransactionService service = new TransactionService();

        // 1. Generating keys for user and miner
        KeyPair miner = service.generateKeyPair(KEY_PAIR_SIZE);
        KeyPair user = service.generateKeyPair(KEY_PAIR_SIZE);

        // 2. Creating coinbase transaction
        Transaction coinbase = service.createCoinbase(miner.getPublic());
        System.out.println("Coinbase transaction: " + coinbase.getId());

        // 3. Miner transfer all amount to user
        Transaction tx1 = service.createTransaction(coinbase, user.getPublic(), miner.getPrivate(), 0);
        System.out.println("Transfer transaction: " + tx1.getId());

        // 4. Transaction verification
        boolean valid = service.verifyTransaction(tx1);
        System.out.println("Transaction valid? " + valid);
    }
}
