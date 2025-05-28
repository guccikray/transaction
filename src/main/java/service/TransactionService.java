package service;

import jakarta.validation.constraints.NotNull;
import signer.Signer;
import transaction.Input;
import transaction.Output;
import transaction.Transaction;

import java.security.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionService {

    private static final String KEY_GENERATING_ALGORITHM = "RSA";
    private static final long ZERO_TRANSACTION_AMOUNT = 100_000;
    public static final int KEY_PAIR_SIZE = 2048;

    private final Map<String, Transaction> blockchainTransactions = new HashMap<>();

    public Transaction createTransaction(
        @NotNull Transaction prevTransaction,
        @NotNull PublicKey recipientPublicKey,
        @NotNull PrivateKey senderPrivateKey,
        int outputIndex
    ) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {

        Output spentOutput = prevTransaction.getOutputs().get(outputIndex);
        long amount = spentOutput.amount();
        Output newOutput = new Output(amount, recipientPublicKey);

        String dataToSign = prevTransaction.getId() +
            outputIndex +
            recipientPublicKey.toString() +
            amount;

        byte[] signature = Signer.generateSignature(dataToSign, senderPrivateKey);

        Input input = new Input(
            prevTransaction.getId(),
            outputIndex,
            signature
        );

        Transaction tx = new Transaction(List.of(input), List.of(newOutput));
        blockchainTransactions.put(tx.getId(), tx);
        return tx;
    }

    public Transaction createCoinbase(@NotNull PublicKey receiverPublicKey) {
        Output output = new Output(ZERO_TRANSACTION_AMOUNT, receiverPublicKey);
        Transaction coinbase = new Transaction(List.of(), List.of(output));
        blockchainTransactions.put(coinbase.getId(), coinbase);
        return coinbase;
    }

    public KeyPair generateKeyPair(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_GENERATING_ALGORITHM);
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.generateKeyPair();
    }

    public Output findOutput(@NotNull String txId, int outputIndex) {
        Transaction tx = blockchainTransactions.get(txId);
        if (tx == null || outputIndex < 0 || outputIndex >= tx.getOutputs().size()) {
            throw new IllegalArgumentException("Output not found");
        }
        return tx.getOutputs().get(outputIndex);
    }

    public boolean verifyTransaction(@NotNull Transaction tx) throws NoSuchAlgorithmException,
        InvalidKeyException,
        SignatureException
    {
        if (tx.getInputs().isEmpty()) return true;

        for (Input input : tx.getInputs()) {
            Output referencedOutput = findOutput(input.prevTransactionId(), input.outputIndex());
            String data = input.prevTransactionId() + input.outputIndex() +
                tx.getOutputs().get(0).recipientPublicKey().toString() +
                tx.getOutputs().get(0).amount();

            if (!Signer.verifySignature(data, input.signature(), referencedOutput.recipientPublicKey())) {
                return false;
            }
        }
        return true;
    }
}
