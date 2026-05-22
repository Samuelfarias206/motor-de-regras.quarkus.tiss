package io.trustep.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DMNConfig {

    private static final String DMN_PATH = "src/main/resources";//

    @Produces
    @ApplicationScoped
    public DMNRuntime dmnRuntime() {
        try {
            // Listar todos os arquivos DMN
            Collection<Resource> dmnFiles = Files.walk(Paths.get(DMN_PATH))
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".dmn"))
                    .map(Path::toFile)
                    .map(DMNConfig::kieServices)
                    .collect(Collectors.toList());

            // Criar DMN Runtime a partir dos arquivos
            return DMNRuntimeBuilder.fromDefaults()
                    .buildConfiguration()
                    .fromResources(dmnFiles)
                    .getOrElseThrow(e -> new RuntimeException("Erro ao carregar DMN", e));

        } catch (Exception e) {
            throw new RuntimeException("Falha na inicialização do DMN", e);
        }
    }

    private static Resource kieServices(File file) {
        final var kieServices = KieServices.Factory.get();
        return kieServices.getResources().newFileSystemResource(file);
    }
}
