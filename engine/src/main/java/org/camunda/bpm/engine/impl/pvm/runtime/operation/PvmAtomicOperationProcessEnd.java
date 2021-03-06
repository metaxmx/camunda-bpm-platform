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

package org.camunda.bpm.engine.impl.pvm.runtime.operation;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.pvm.delegate.SubProcessActivityBehavior;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.pvm.runtime.PvmExecutionImpl;


/**
 * @author Tom Baeyens
 */
public class PvmAtomicOperationProcessEnd extends PvmAtomicOperationActivityInstanceEnd {

  private static Logger log = Logger.getLogger(PvmAtomicOperationProcessEnd.class.getName());

  protected ScopeImpl getScope(PvmExecutionImpl execution) {
    return execution.getProcessDefinition();
  }

  protected String getEventName() {
    return ExecutionListener.EVENTNAME_END;
  }

  @Override
  protected void eventNotificationsCompleted(PvmExecutionImpl execution) {

    super.eventNotificationsCompleted(execution);

    PvmExecutionImpl superExecution = execution.getSuperExecution();
    SubProcessActivityBehavior subProcessActivityBehavior = null;

    // copy variables before destroying the ended sub process instance
    if (superExecution!=null) {
      ActivityImpl activity = superExecution.getActivity();
      subProcessActivityBehavior = (SubProcessActivityBehavior) activity.getActivityBehavior();
      try {
        subProcessActivityBehavior.completing(superExecution, execution);
      } catch (RuntimeException e) {
          log.log(Level.SEVERE, "Error while completing sub process of execution " + execution, e);
          throw e;
      } catch (Exception e) {
          log.log(Level.SEVERE, "Error while completing sub process of execution " + execution, e);
          throw new ProcessEngineException("Error while completing sub process of execution " + execution, e);
      }
    }

    execution.destroy();
    execution.remove();

    // and trigger execution afterwards
    if (superExecution!=null) {
      superExecution.setSubProcessInstance(null);
      try {
          subProcessActivityBehavior.completed(superExecution);
      } catch (RuntimeException e) {
          log.log(Level.SEVERE, "Error while completing sub process of execution " + execution, e);
          throw e;
      } catch (Exception e) {
          log.log(Level.SEVERE, "Error while completing sub process of execution " + execution, e);
          throw new ProcessEngineException("Error while completing sub process of execution " + execution, e);
      }
    }
  }

  public String getCanonicalName() {
    return "process-end";
  }
}
