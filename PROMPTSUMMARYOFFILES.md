The "Weight Domain" Architectural Manifest
Context: I am working with a tri-part Weight Domain in Java 25. Treat these rules as the "Always-Valid" source of truth:

    WeightConstants (The Boundaries):
        Limits: [0.001 to 100,000.0] Grams.
        DoS Protection: Max input scale is 10.
        Precision: Normalization scale 4, Internal/Comparison scale 8.
    WeightUnitEnums (The Logic):
        Units: GRAM, KILOGRAM, POUND, OUNCE, CARAT, TROY_OUNCE.
        Mechanism: Handles bidirectional conversion to/from Grams using INTERNAL_MATH_CONTEXT.
    WeightVO (The Record):
        State: BigDecimal amount, WeightUnitEnums unit.
        Contract: Compact constructor enforces boundaries (Gram-limit check), nullability, and non-negativity.
        Equality: Automatically normalizes amount to scale 4 to ensure canonical Record equality.

Task: [INSERT YOUR REQUEST HERE, e.g., "Add a method to WeightVO to add two weights together safely"]