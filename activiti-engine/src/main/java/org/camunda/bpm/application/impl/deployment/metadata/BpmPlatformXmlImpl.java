/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.application.impl.deployment.metadata;

import java.util.List;

import org.camunda.bpm.application.impl.deployment.metadata.spi.BpmPlatformXml;
import org.camunda.bpm.application.impl.deployment.metadata.spi.JobExecutorXml;
import org.camunda.bpm.application.impl.deployment.metadata.spi.ProcessEngineXml;

/**
 * <p>Implementation of the BpmPlatformXml SPI</p>
 * 
 * @author Daniel Meyer
 * 
 */
public class BpmPlatformXmlImpl implements BpmPlatformXml {

  protected JobExecutorXml jobExecutor;

  protected List<ProcessEngineXml> processEngines;
  
  public BpmPlatformXmlImpl(JobExecutorXml jobExecutor, List<ProcessEngineXml> processEngines) {
    this.jobExecutor = jobExecutor;
    this.processEngines = processEngines;
  }

  public List<ProcessEngineXml> getProcessEngines() {
    return processEngines;
  }

  public void setProcessEngines(List<ProcessEngineXml> processEngines) {
    this.processEngines = processEngines;
  }

  public JobExecutorXml getJobExecutor() {
    return jobExecutor;
  }

  public void setJobExecutor(JobExecutorXml jobExecutor) {
    this.jobExecutor = jobExecutor;
  }

}