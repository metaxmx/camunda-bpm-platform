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
package org.camunda.bpm.engine.impl.cmmn.operation;

import java.util.logging.Logger;

import org.camunda.bpm.engine.impl.cmmn.execution.CmmnActivityBehavior;
import org.camunda.bpm.engine.impl.cmmn.execution.CmmnExecution;
import org.camunda.bpm.engine.impl.cmmn.model.CmmnActivity;
import org.camunda.bpm.engine.impl.pvm.PvmException;

/**
 * @author Tom Baeyens
 */
public class AtomicOperationActivityExecute implements CmmnAtomicOperation {

  private static Logger log = Logger.getLogger(AtomicOperationActivityExecute.class.getName());

  public boolean isAsync(CmmnExecution execution) {
    return false;
  }

  public void execute(CmmnExecution execution) {
    CmmnActivity activity = execution.getActivity();

    CmmnActivityBehavior activityBehavior = activity.getActivityBehavior();
    if (activityBehavior==null) {
      throw new PvmException("no behavior specified in "+activity);
    }

    log.fine(execution+" executes "+activity+": "+activityBehavior.getClass().getName());

    try {
      activityBehavior.execute(execution);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new PvmException("couldn't execute activity <"+activity.getProperty("type")+" id=\""+activity.getId()+"\" ...>: "+e.getMessage(), e);
    }
  }

  public String getCanonicalName() {
    return "activity-execute";
  }
}
