package transaction;

import jakarta.validation.constraints.NotNull;

import java.util.*;

/**
 * Class is NOT thread-safe!
 */
public class Transaction {

    private final String id;
    private final List<Input> inputs;
    private final List<Output> outputs;

    public Transaction(List<Input> inputs, List<Output> outputs) {
        this.id = UUID.randomUUID().toString();
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public List<Input> getInputs() {
        return Collections.unmodifiableList(inputs);
    }

    public List<Output> getOutputs() {
        return Collections.unmodifiableList(outputs);
    }

    public String getId() {
        return id;
    }

    public Transaction addInput(@NotNull Input input) {
        this.inputs.add(input);
        return this;
    }

    public Transaction addOutput(@NotNull Output output) {
        this.outputs.add(output);
        return this;
    }
}
