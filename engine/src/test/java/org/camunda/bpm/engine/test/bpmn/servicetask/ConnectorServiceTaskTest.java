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
package org.camunda.bpm.engine.test.bpmn.servicetask;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.connect.soap.httpclient.SoapHttpConnector;
import org.camunda.bpm.engine.impl.test.PluggableProcessEngineTestCase;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.bpmn.servicetask.util.TestConnector;

/**
 * @author Daniel Meyer
 *
 */
public class ConnectorServiceTaskTest extends PluggableProcessEngineTestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    processEngineConfiguration.getConnectors().addConnector("testConnector", TestConnector.class);
    TestConnector.responseParameters.clear();
    TestConnector.requestParameters = null;
  }

  @Deployment
  public void testConnectorInvoked() {

    String outputParamValue = "someOutputValue";
    String inputVariableValue = "someInputVariableValue";

    TestConnector.responseParameters.put("someOutputParameter", outputParamValue);

    Map<String, Object> vars = new HashMap<String, Object>();
    vars.put("someInputVariable", inputVariableValue);
    runtimeService.startProcessInstanceByKey("testProcess", vars);

    // validate input parameter
    assertNotNull(TestConnector.requestParameters.get("reqParam1"));
    assertEquals(inputVariableValue, TestConnector.requestParameters.get("reqParam1"));

    // validate connector output
    VariableInstance variable = runtimeService.createVariableInstanceQuery().variableName("out1").singleResult();
    assertNotNull(variable);
    assertEquals(outputParamValue, variable.getValue());

  }

  public void testDefaultConnectorsRegistered() {
    assertEquals(SoapHttpConnector.class, processEngineConfiguration.getConnectors().getConnector(SoapHttpConnector.ID));
  }

}
