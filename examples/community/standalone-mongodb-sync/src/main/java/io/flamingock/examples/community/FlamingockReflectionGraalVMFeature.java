package io.flamingock.examples.community;

import io.flamingock.core.util.FileUtil;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;

import java.util.List;

public class FlamingockReflectionGraalVMFeature implements Feature {

    public void beforeAnalysis(BeforeAnalysisAccess access) {
        try {

            String fileName = "flamingock/change-units-list.txt";
            List<String> files = FileUtil.readLinesFromFile(fileName);

            if(files.size() == 0) {
                throw new RuntimeException(String.format("File %s not found", fileName));
            }
            for(String className : files) {
                Class<?> clazz = Class.forName(className);
                RuntimeReflection.register(clazz);
                RuntimeReflection.register(clazz.getDeclaredConstructors());
                RuntimeReflection.register(clazz.getDeclaredMethods());
            }


        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


}
