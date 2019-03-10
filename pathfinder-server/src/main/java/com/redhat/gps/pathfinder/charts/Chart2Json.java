package com.redhat.gps.pathfinder.charts;

/*-
 * #%L
 * com.redhat.gps.pathfinder.pathfinder-server
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2018 - 2019 RedHat Inc
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
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
 * #L%
 */

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Chart2Json {
  private Set<String> labels=new LinkedHashSet<String>();
  private List<DataSet2> datasets=new ArrayList<DataSet2>();
  
  public Set<String> getLabels() {
    return labels;
  }
  public void setLabels(Set<String> labels) {
    this.labels=labels;
  }
  public List<DataSet2> getDatasets() {
    return datasets;
  }
  public void setDatasets(List<DataSet2> datasets) {
    this.datasets=datasets;
  }
  
  
}
