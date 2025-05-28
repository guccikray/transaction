package transaction;

import java.security.PublicKey;

public record Output(long amount, PublicKey recipientPublicKey) {
}
