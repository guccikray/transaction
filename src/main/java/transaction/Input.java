package transaction;

public record Input(String prevTransactionId, int outputIndex, byte[] signature) {

}
