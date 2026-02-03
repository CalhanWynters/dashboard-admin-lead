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


1. ProductAggregateRoot

   ProductCreatedEvent: Emitted by ProductFactory.
   ProductStatusChangedEvent: Captured during performStatusTransition; crucial for state-machine logic.
   ProductManifestUpdatedEvent: Fired when ProductManifest (Name, Category, Description) changes.
   ProductVersionIncrementedEvent: Fired on incrementVersion for granular auditing.
   ProductDeletedEvent: Signals a Soft Delete; triggers archival in linked Gallery and PriceList modules.

2. GalleryAggregate

   GalleryCreatedEvent: Initialization signal.
   GalleryTouchedEvent: Generic update signal for internal changes.
   GalleryDeletedEvent: Informs the Product module to set its GalleryUuId to NONE.

3. ImageAggregate

   ImageUploadedEvent: (Captured in Factory) Confirms the aggregate is ready for use.
   ImageMetadataUpdatedEvent: Fired when Name or Description changes.
   ImageDeletedEvent: Signals the Gallery module to remove this UUID from all active sets.

4. PriceListAggregate

   PriceListCreatedEvent: New pricing boundary defined.
   PriceUpdatedEvent: Specific Currency/Value delta; triggers recalculations in open Carts.
   PriceRemovedEvent: Signals a specific currency is no longer supported for a target.
   PriceListVersionIncrementedEvent: Tracks historical shifts in the pricing strategy.
   PriceListDeletedEvent: A "Stop Sale" signal; triggers cleanup in the Product module.

5. FeaturesAggregate

   FeatureCreatedEvent: New capability available for variants.
   FeatureDetailsUpdatedEvent: Fired when Name or Compatibility Tag (Label) changes.
   FeatureDeletedEvent: Critical Cleanup Signal for the Variants module to purge this ID from all assigned sets.

6. Types & Variants Aggregates

   TypeCreatedEvent / VariantCreatedEvent: Initial system entry.
   TypeRenamedEvent / VariantRenamedEvent: UI-facing identity shifts.
   TypePhysicalSpecsUpdatedEvent: Triggers Shipping/Warehouse rate recalculations.
   FeatureAssignedEvent / FeatureUnassignedEvent: Updates filtering logic in Catalog Search.
   TypeDeletedEvent / VariantDeletedEvent: Signals TypeList or VariantList to detach the ID.

7. TypeList & VariantList Aggregates

   TypeAttachedEvent / TypeDetachedEvent: Controls Product-to-Blueprint relationships.
   VariantAttachedEvent / VariantDetachedEvent: Informs Inventory of active/inactive SKUs.
   ListPurgedEvent: Fired on soft deletion of the list container.

Strategic Integration Note
To maintain a "Pure Domain," do not inject repositories into your aggregates to handle these cleanup side effects. Instead, use a TransactionalEventListener in the receiving module. This keeps your domain logic decoupled and ensures that cleanup only happens if the primary deletion transaction commits.
For more on reliable event delivery, see the Spring Modulith Event Publication Registry.
Which aggregate's event records should we implement next? We can start with the Product or PriceList events to see how to include the specific UuId and Actor data.
