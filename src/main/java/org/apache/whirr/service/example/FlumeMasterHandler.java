/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.whirr.service.example;

import static org.apache.whirr.RolePredicates.role;
import static org.jclouds.scriptbuilder.domain.Statements.call;

import java.io.IOException;

import org.apache.whirr.Cluster;
import org.apache.whirr.Cluster.Instance;
import org.apache.whirr.service.ClusterActionEvent;
import org.apache.whirr.service.ClusterActionHandlerSupport;
import org.apache.whirr.service.FirewallManager.Rule;

public class FlumeMasterHandler extends ClusterActionHandlerSupport {

  public static final String ROLE = "flumedemo-master";
  private static final int CLIENT_PORT = 35871;
  
  @Override public String getRole() { return ROLE; }
  
  @Override
  protected void beforeBootstrap(ClusterActionEvent event) throws IOException,
      InterruptedException {
    addStatement(event, call("install_java"));
    addStatement(event, call("install_flumedemo"));
  }
  
  @Override
  protected void beforeConfigure(ClusterActionEvent event) throws IOException,
      InterruptedException {
    event.getFirewallManager().addRule(
        Rule.create().destination(role(ROLE)).port(CLIENT_PORT)
    );
    
    addStatement(event, call("configure_flumedemo_master"));
  }
  
  @Override
  protected void afterConfigure(ClusterActionEvent event) throws IOException,
      InterruptedException {
    Cluster cluster = event.getCluster();
    Instance master = cluster.getInstanceMatching(role(ROLE));
    String masterAddress = master.getPublicAddress().getHostName();
    System.out.printf("Flume master UI at http://%s:%s\n",
        masterAddress, CLIENT_PORT);
  }

}
