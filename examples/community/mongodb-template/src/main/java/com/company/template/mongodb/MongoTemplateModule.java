package com.company.template.mongodb;


import io.flamingock.template.TemplateModule;
import io.flamingock.template.TemplateSpec;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MongoTemplateModule implements TemplateModule {

    private static final Set<TemplateSpec> templates;

    static {
        HashSet<TemplateSpec> templatesSet = new HashSet<>();
        templatesSet.add(new TemplateSpec("mongodb/create-collection-template", MongoCreateCollectionTemplate.class));
        templates = Collections.unmodifiableSet(templatesSet);
    }

    @Override
    public Set<TemplateSpec> getTemplates() {
        return templates;
    }
}