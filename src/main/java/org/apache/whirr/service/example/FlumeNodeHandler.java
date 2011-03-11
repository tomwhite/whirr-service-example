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

import static org.apache.whirr.service.RolePredicates.role;
import static org.jclouds.scriptbuilder.domain.Statements.call;

import java.io.IOException;

import org.apache.whirr.service.Cluster;
import org.apache.whirr.service.Cluster.Instance;
import org.apache.whirr.service.ClusterActionEvent;
import org.apache.whirr.service.ClusterActionHandlerSupport;
import org.apache.whirr.service.ClusterSpec;
import org.apache.whirr.service.ComputeServiceContextBuilder;
import org.apache.whirr.service.jclouds.FirewallSettings;
import org.jclouds.compute.ComputeServiceContext;

public class FlumeNodeHandler extends ClusterActionHandlerSupport {

  public static final String ROLE = "flumedemo-node";
  private static final int CLIENT_PORT = 35872;
  
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
    ClusterSpec clusterSpec = event.getClusterSpec();
    Cluster cluster = event.getCluster();
    ComputeServiceContext computeServiceContext =
      ComputeServiceContextBuilder.build(clusterSpec);
    FirewallSettings.authorizeIngress(computeServiceContext,
        cluster.getInstancesMatching(role(ROLE)), clusterSpec, CLIENT_PORT);

    Instance master = cluster.getInstanceMatching(role(FlumeMasterHandler.ROLE));
    String masterAddress = master.getPrivateAddress().getHostAddress();
    addStatement(event, call("configure_flumedemo_node", masterAddress));
  }

}
