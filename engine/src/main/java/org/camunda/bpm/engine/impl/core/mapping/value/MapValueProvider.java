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
package org.camunda.bpm.engine.impl.core.mapping.value;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.camunda.bpm.engine.impl.core.variable.CoreVariableScope;

/**
 * @author Daniel Meyer
 *
 */
public class MapValueProvider implements ParameterValueProvider {

  protected TreeMap<String, ParameterValueProvider> providerMap;

  public MapValueProvider(TreeMap<String, ParameterValueProvider> providerMap) {
    this.providerMap = providerMap;
  }

  public Object getValue(CoreVariableScope variableScope) {
    Map<String, Object> valueMap = new TreeMap<String, Object>();
    for (Entry<String, ParameterValueProvider> entry : providerMap.entrySet()) {
      valueMap.put(entry.getKey(), entry.getValue().getValue(variableScope));
    }
    return valueMap;
  }

  public TreeMap<String, ParameterValueProvider> getProviderMap() {
    return providerMap;
  }

  public void setProviderMap(TreeMap<String, ParameterValueProvider> providerMap) {
    this.providerMap = providerMap;
  }

}
