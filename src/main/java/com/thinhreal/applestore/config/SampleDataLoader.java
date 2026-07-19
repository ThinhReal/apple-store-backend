package com.thinhreal.applestore.config;

import com.thinhreal.applestore.model.entity.CategoryEntity;
import com.thinhreal.applestore.model.entity.FlavorProfileValue;
import com.thinhreal.applestore.model.entity.ProductEntity;
import com.thinhreal.applestore.repository.CategoryRepository;
import com.thinhreal.applestore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SampleDataLoader implements CommandLineRunner {

    private static final String GIFT_BOXES = "Gift Boxes";
    private static final String CIDER_AND_JUICE = "Cider & Juice";

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        CategoryEntity giftBoxes = ensureCategory(
                GIFT_BOXES,
                "Curated apple gift sets for every occasion"
        );
        CategoryEntity ciderAndJuice = ensureCategory(
                CIDER_AND_JUICE,
                "Fresh-pressed ciders and orchard juices"
        );

        if (productRepository.count() > 0) {
            log.info("Sample products already present, skipping product seed");
            return;
        }

        ProductEntity heritageGiftBox = new ProductEntity();
        heritageGiftBox.setName("Heritage Apple Gift Box");
        heritageGiftBox.setDescription("A premium selection of hand-picked heritage apple varieties.");
        heritageGiftBox.setPrice(new BigDecimal("49.99"));
        heritageGiftBox.setStockQuantity(25);
        heritageGiftBox.setCategory(giftBoxes);
        heritageGiftBox.setCategoryName(giftBoxes.getName());
        heritageGiftBox.setOrigin("Hudson Valley, NY");
        heritageGiftBox.setSeason("Late fall");
        heritageGiftBox.setImageUrl("https://images.unsplash.com/photo-1510627489930-0c1b0bfb6785?w=900&h=900&fit=crop&auto=format");
        heritageGiftBox.setTastingNotes(List.of("honeyed", "firm", "aromatic"));
        heritageGiftBox.setBestFor(List.of("gifts", "charcuterie boards"));

        FlavorProfileValue giftBoxProfile = new FlavorProfileValue();
        giftBoxProfile.setSweetnessLevel(7);
        giftBoxProfile.setTartnessLevel(3);
        giftBoxProfile.setOverallProfile("Balanced and elegant");
        giftBoxProfile.setDominantNotes(List.of("honeycrisp", "fuji", "gala"));
        giftBoxProfile.setTastingDescription("A polished mix of sweet and crisp orchard apples.");
        heritageGiftBox.setFlavorProfile(giftBoxProfile);

        ProductEntity sparklingCider = new ProductEntity();
        sparklingCider.setName("Sparkling Apple Cider");
        sparklingCider.setDescription("Crisp, lightly sparkling cider made from estate-grown apples.");
        sparklingCider.setPrice(new BigDecimal("12.99"));
        sparklingCider.setStockQuantity(80);
        sparklingCider.setCategory(ciderAndJuice);
        sparklingCider.setCategoryName(ciderAndJuice.getName());
        sparklingCider.setOrigin("Sonoma County, CA");
        sparklingCider.setSeason("Fall harvest");
        sparklingCider.setImageUrl("https://images.unsplash.com/photo-1535914254981-b5012eebbd15?w=900&h=900&fit=crop&auto=format");
        sparklingCider.setTastingNotes(List.of("crisp", "apple", "light sparkle"));
        sparklingCider.setBestFor(List.of("dinner parties", "cheese boards"));

        FlavorProfileValue ciderProfile = new FlavorProfileValue();
        ciderProfile.setSweetnessLevel(6);
        ciderProfile.setTartnessLevel(4);
        ciderProfile.setOverallProfile("Bright and refreshing");
        ciderProfile.setDominantNotes(List.of("green apple", "citrus"));
        ciderProfile.setTastingDescription("Clean finish with a hint of natural sweetness.");
        sparklingCider.setFlavorProfile(ciderProfile);

        productRepository.save(heritageGiftBox);
        productRepository.save(sparklingCider);

        log.info("Inserted sample data: 2 products (categories ensured)");
    }

    private CategoryEntity ensureCategory(String name, String description) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> {
                    CategoryEntity category = new CategoryEntity();
                    category.setName(name);
                    category.setDescription(description);
                    return categoryRepository.save(category);
                });
    }
}
