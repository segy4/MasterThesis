package Suppliers;

import java.util.function.Supplier;

public class ConstantSupplier implements Supplier<Double> {

    private final Double value;

    public ConstantSupplier(Double returnValue) {
        value = returnValue;
    }

    @Override
    public Double get() {
        return value;
    }
}
