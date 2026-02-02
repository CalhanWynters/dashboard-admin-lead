# TO DO LIST
- Check UuIds in classes for confusion with java.util.UUID
- Handle Feature/Type compatibility on domain service. Only looking to snapshot to main business core microservice.
    That is where the handling of compatibility will be coded.
- Re-examine the validation for O [Currency] on args with "~~perunit" in the domain PurchasePricing concrete classes.
- Carefully look over the refractored codes for Variant and VariantCollection
- Build the JPA AttributeConverters to map these microscopic NONE values (like 0.0000000001) to database NULL columns





- Plan out SKU Domain. Here are some references:
  - https://retailedge.com/docs/a-customers-story-on-using-strict-naming-conventions-instead-of-classes-in-retailedge
  - https://www.symbia.com/resources/productAggregateRoot-skus/



    