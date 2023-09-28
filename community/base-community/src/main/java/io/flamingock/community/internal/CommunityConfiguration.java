package io.flamingock.community.internal;

public class CommunityConfiguration implements CommunityConfigurable {

    private boolean indexCreation = true;

    @Override
    public boolean isIndexCreation() {
        return indexCreation;
    }

    @Override
    public void setIndexCreation(boolean value) {
        this.indexCreation = value;
    }
}
