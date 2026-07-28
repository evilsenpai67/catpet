package com.zenuxs.catpets.data;

public enum CatType {
    JAMES("James", "james", 1),
    BOB("Bob", "bob", 2),
    LARRY("Larry", "larry", 3);

    private final String displayName;
    private final String textureName;
    private final int entityVariant;

    CatType(String displayName, String textureName, int entityVariant) {
        this.displayName = displayName;
        this.textureName = textureName;
        this.entityVariant = entityVariant;
    }

    public String getDisplayName() { return displayName; }
    public String getTextureName() { return textureName; }
    public int getEntityVariant() { return entityVariant; }

    public static CatType getRandom() {
        CatType[] values = values();
        return values[new java.util.Random().nextInt(values.length)];
    }

    public static CatType fromString(String str) {
        for (CatType type : values()) {
            if (type.name().equalsIgnoreCase(str) || type.textureName.equalsIgnoreCase(str)) {
                return type;
            }
        }
        return null;
    }
}