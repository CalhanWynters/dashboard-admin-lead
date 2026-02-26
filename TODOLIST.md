# TO DO LIST
- Check UuIds in classes for confusion with java.util.UUID
- Handle Feature/Type compatibility on domain/application service. 
- FeatureCompatibilityChangedEvent Using Fail Fast principles, domain/application service will look for variants that will cause issues. Developers see variants.
- Re-examine the validation for O [Currency] on args with "~~perunit" in the domain PurchasePricing concrete classes.
- Carefully look over the refractored codes for Variant and VariantCollection
- Build the JPA AttributeConverters to map these microscopic NONE values (like 0.0000000001) to database NULL columns
- Add GalleryAggregate Thumbnail
- Check Aggregate behaviors and any other files for proper error codes.
- Evaluate code during review for time based optimistic locking? or consider another method of optimistic locking.
- Evaluate mutability of fields in aggregate roots and possible mechanisms of read and write.


- Plan out SKU Domain. Here are some references:
  - https://retailedge.com/docs/a-customers-story-on-using-strict-naming-conventions-instead-of-classes-in-retailedge
  - https://www.symbia.com/resources/productAggregateRoot-skus/

- Refine Domain Layer Code to be a strong foundation for SOC II compliance.
- Refine Application Code to be SOC II compliant
- Refine Infrastructure Code to be SOC II compliant