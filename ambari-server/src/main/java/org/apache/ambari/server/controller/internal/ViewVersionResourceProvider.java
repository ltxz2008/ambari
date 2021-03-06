/*
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

package org.apache.ambari.server.controller.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ambari.server.controller.ViewVersionResponse;
import org.apache.ambari.server.controller.spi.NoSuchParentResourceException;
import org.apache.ambari.server.controller.spi.NoSuchResourceException;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.RequestStatus;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceAlreadyExistsException;
import org.apache.ambari.server.controller.spi.SystemException;
import org.apache.ambari.server.controller.spi.UnsupportedPropertyException;
import org.apache.ambari.server.orm.entities.ViewEntity;
import org.apache.ambari.server.view.ViewRegistry;
import org.apache.ambari.server.view.configuration.ParameterConfig;
import org.apache.ambari.view.ViewDefinition;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * Resource provider for view versions.
 */
public class ViewVersionResourceProvider extends AbstractResourceProvider {

  /**
   * View property id constants.
   */
  public static final String VIEW_NAME_PROPERTY_ID          = "ViewVersionInfo/view_name";
  public static final String VIEW_VERSION_PROPERTY_ID       = "ViewVersionInfo/version";
  public static final String VIEW_BUILD_PROPERTY_ID         = "ViewVersionInfo/build_number";
  public static final String LABEL_PROPERTY_ID              = "ViewVersionInfo/label";
  public static final String DESCRIPTION_PROPERTY_ID        = "ViewVersionInfo/description";
  public static final String MIN_AMBARI_VERSION_PROPERTY_ID = "ViewVersionInfo/min_ambari_version";
  public static final String MAX_AMBARI_VERSION_PROPERTY_ID = "ViewVersionInfo/max_ambari_version";
  public static final String PARAMETERS_PROPERTY_ID         = "ViewVersionInfo/parameters";
  public static final String ARCHIVE_PROPERTY_ID            = "ViewVersionInfo/archive";
  public static final String MASKER_CLASS_PROPERTY_ID       = "ViewVersionInfo/masker_class";
  public static final String VIEW_STATUS_PROPERTY_ID        = "ViewVersionInfo/status";
  public static final String VIEW_STATUS_DETAIL_PROPERTY_ID = "ViewVersionInfo/status_detail";
  public static final String CLUSTER_CONFIG_PROPERTY_ID     = "ViewVersionInfo/cluster_configurable";
  public static final String SYSTEM_PROPERTY_ID             = "ViewVersionInfo/system";

  /**
   * The key property ids for a view resource.
   */
  private static Map<Resource.Type, String> keyPropertyIds = ImmutableMap.<Resource.Type, String>builder()
      .put(Resource.Type.View, VIEW_NAME_PROPERTY_ID)
      .put(Resource.Type.ViewVersion, VIEW_VERSION_PROPERTY_ID)
      .build();

  /**
   * The property ids for a view resource.
   */
  private static Set<String> propertyIds = Sets.newHashSet(
      VIEW_NAME_PROPERTY_ID,
      VIEW_VERSION_PROPERTY_ID,
      VIEW_BUILD_PROPERTY_ID,
      LABEL_PROPERTY_ID,
      DESCRIPTION_PROPERTY_ID,
      MIN_AMBARI_VERSION_PROPERTY_ID,
      MAX_AMBARI_VERSION_PROPERTY_ID,
      PARAMETERS_PROPERTY_ID,
      ARCHIVE_PROPERTY_ID,
      MASKER_CLASS_PROPERTY_ID,
      VIEW_STATUS_PROPERTY_ID,
      VIEW_STATUS_DETAIL_PROPERTY_ID,
      CLUSTER_CONFIG_PROPERTY_ID,
      SYSTEM_PROPERTY_ID);


  // ----- Constructors ------------------------------------------------------

  /**
   * Construct a view resource provider.
   */
  public ViewVersionResourceProvider() {
    super(propertyIds, keyPropertyIds);
  }


  // ----- ResourceProvider --------------------------------------------------

  @Override
  public RequestStatus createResources(Request request)
      throws SystemException, UnsupportedPropertyException,
      ResourceAlreadyExistsException, NoSuchParentResourceException {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public Set<Resource> getResources(Request request, Predicate predicate)
      throws SystemException, UnsupportedPropertyException, NoSuchResourceException, NoSuchParentResourceException {
    Set<Resource> resources    = new HashSet<>();
    ViewRegistry  viewRegistry = ViewRegistry.getInstance();
    Set<String>   requestedIds = getRequestPropertyIds(request, predicate);

    Set<Map<String, Object>> propertyMaps = getPropertyMaps(predicate);

    if (propertyMaps.isEmpty()) {
      propertyMaps.add(Collections.emptyMap());
    }

    for (Map<String, Object> propertyMap : propertyMaps) {

      String viewName    = (String) propertyMap.get(VIEW_NAME_PROPERTY_ID);
      String viewVersion = (String) propertyMap.get(VIEW_VERSION_PROPERTY_ID);

      for (ViewEntity viewDefinition : viewRegistry.getDefinitions()){
        if (viewName == null || viewName.equals(viewDefinition.getCommonName())) {
          if (viewVersion == null || viewVersion.equals(viewDefinition.getVersion())) {
            Resource resource = new ResourceImpl(Resource.Type.ViewVersion);
            ViewVersionResponse viewVersionResponse = getResponse(viewDefinition);
            ViewVersionResponse.ViewVersionInfo viewVersionInfo = viewVersionResponse.getViewVersionInfo();
            setResourceProperty(resource, VIEW_NAME_PROPERTY_ID, viewVersionInfo.getViewName(), requestedIds);
            setResourceProperty(resource, VIEW_VERSION_PROPERTY_ID, viewVersionInfo.getVersion(), requestedIds);
            setResourceProperty(resource, VIEW_BUILD_PROPERTY_ID, viewVersionInfo.getBuildNumber(), requestedIds);
            setResourceProperty(resource, LABEL_PROPERTY_ID, viewVersionInfo.getLabel(), requestedIds);
            setResourceProperty(resource, DESCRIPTION_PROPERTY_ID, viewVersionInfo.getDescription(), requestedIds);
            setResourceProperty(resource, MIN_AMBARI_VERSION_PROPERTY_ID,
              viewVersionInfo.getMinAmbariVersion(), requestedIds);
            setResourceProperty(resource, MAX_AMBARI_VERSION_PROPERTY_ID,
              viewVersionInfo.getMaxAmbariVersion(), requestedIds);
            setResourceProperty(resource, PARAMETERS_PROPERTY_ID,
              viewVersionInfo.getParameters(), requestedIds);
            setResourceProperty(resource, ARCHIVE_PROPERTY_ID, viewVersionInfo.getArchive(), requestedIds);
            setResourceProperty(resource, MASKER_CLASS_PROPERTY_ID, viewVersionInfo.getMaskerClass(), requestedIds);
            setResourceProperty(resource, VIEW_STATUS_PROPERTY_ID, viewVersionInfo.getStatus().toString(), requestedIds);
            setResourceProperty(resource, VIEW_STATUS_DETAIL_PROPERTY_ID, viewVersionInfo.getStatusDetail(), requestedIds);
            setResourceProperty(resource, CLUSTER_CONFIG_PROPERTY_ID, viewVersionInfo.isClusterConfigurable(), requestedIds);
            setResourceProperty(resource, SYSTEM_PROPERTY_ID, viewVersionInfo.isSystem(), requestedIds);

            resources.add(resource);
          }
        }
      }
    }
    return resources;
  }

  /**
   * Returns response schema instance for view version REST endpoint: /views/{viewName}/versions
   * @param viewDefinition   view entity {@link ViewEntity}
   * @return {@link ViewVersionResponse}
   */
  public ViewVersionResponse getResponse(ViewEntity viewDefinition) {
    String archive = viewDefinition.getArchive();
    String buildNumber = viewDefinition.getBuild();
    boolean clusterConfigurable = viewDefinition.isClusterConfigurable();
    String description = viewDefinition.getDescription();
    String label =  viewDefinition.getLabel();
    String maskerClass = viewDefinition.getMask();
    String maxAmbariVersion = viewDefinition.getConfiguration().getMaxAmbariVersion();
    String minAmbariVersion = viewDefinition.getConfiguration().getMinAmbariVersion();
    List<ParameterConfig> parameters = viewDefinition.getConfiguration().getParameters();
    ViewDefinition.ViewStatus status = viewDefinition.getStatus();
    String statusDetail = viewDefinition.getStatusDetail();
    boolean system =  viewDefinition.isSystem();
    String version =  viewDefinition.getVersion();
    String viewName =  viewDefinition.getCommonName();
    ViewVersionResponse.ViewVersionInfo viewVersionInfo = new ViewVersionResponse.ViewVersionInfo(archive, buildNumber,
      clusterConfigurable, description, label, maskerClass, maxAmbariVersion, minAmbariVersion, parameters, status,
      statusDetail, system, version, viewName);
    return new ViewVersionResponse(viewVersionInfo);
  }

  @Override
  public RequestStatus updateResources(Request request, Predicate predicate)
      throws SystemException, UnsupportedPropertyException, NoSuchResourceException, NoSuchParentResourceException {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public RequestStatus deleteResources(Request request, Predicate predicate)
      throws SystemException, UnsupportedPropertyException, NoSuchResourceException, NoSuchParentResourceException {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public Map<Resource.Type, String> getKeyPropertyIds() {
    return keyPropertyIds;
  }


  // ----- AbstractResourceProvider ------------------------------------------

  @Override
  protected Set<String> getPKPropertyIds() {
    return new HashSet<>(keyPropertyIds.values());
  }
}
