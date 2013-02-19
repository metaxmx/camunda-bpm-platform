package org.camunda.bpm.engine.rest.util;

import java.io.File;
import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.StrongUuidGenerator;
import org.activiti.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.rest.impl.ProcessDefinitionServiceImpl;
import org.camunda.bpm.engine.rest.impl.ProcessInstanceServiceImpl;
import org.camunda.bpm.engine.rest.impl.TaskRestServiceImpl;
import org.camunda.bpm.engine.rest.mapper.EngineQueryDtoGetReader;
import org.camunda.bpm.engine.rest.mapper.JacksonConfigurator;
import org.camunda.bpm.engine.rest.spi.ProcessEngineProvider;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;

/**
 * Enviroment with process engine and embedded HTTP Server to be used in development.
 * 
 * @author Daniel Meyer
 * 
 */
public class DevEnvironmentSetup implements ProcessEngineProvider {

  private static final int numOfProcessesPerDefinition = 10;
  private static ProcessEngine processEngine;

  public static void main(String[] args) {
    createProcessEngine();
    createDemoData();
    startEmbeddedHttpServer();
  }
  
  protected static void createProcessEngine() {
    ProcessEngineConfiguration processEngineConfiguration = 
        ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
    // use UUIDs
    ((ProcessEngineConfigurationImpl)processEngineConfiguration).setIdGenerator(new StrongUuidGenerator());
    processEngine = processEngineConfiguration.buildProcessEngine();
  }


  private static void createDemoData() {
    RepositoryService repositoryService = processEngine.getRepositoryService();
    
    repositoryService
      .createDeployment()
      .addClasspathResource("processes/fox-invoice_en.bpmn")
      .addClasspathResource("processes/fox-invoice_en_long_id.bpmn")
      .deploy();
    
    RuntimeService runtimeService = processEngine.getRuntimeService();
    List<ProcessDefinition> pds = repositoryService.createProcessDefinitionQuery().list();
    for (ProcessDefinition pd : pds) {
      for (int i = 0; i < numOfProcessesPerDefinition; i++) {
        runtimeService.startProcessInstanceById(pd.getId());
      }
    }
  }

  private static void startEmbeddedHttpServer() {
    
    TJWSEmbeddedJaxrsServer server = new TJWSEmbeddedJaxrsServer();

    server.setRootResourcePath("/camunda-engine-rest");
    server.addFileMapping("/cockpit", new File("./src/main/webapp"));

    server.setPort(8081);

    server.getDeployment().getActualResourceClasses().add(ProcessDefinitionServiceImpl.class);
    server.getDeployment().getActualResourceClasses().add(ProcessInstanceServiceImpl.class);
    server.getDeployment().getActualResourceClasses().add(TaskRestServiceImpl.class);

    server.getDeployment().getActualProviderClasses().add(EngineQueryDtoGetReader.class);
    server.getDeployment().getActualProviderClasses().add(JacksonConfigurator.class);

    server.getDeployment().getActualProviderClasses().add(JacksonJsonProvider.class);

    server.start();
    
  }


  public ProcessEngine getProcessEngine() {
    return processEngine;
  }

}