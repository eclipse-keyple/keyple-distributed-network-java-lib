/* **************************************************************************************
 * Copyright (c) 2021 Calypso Networks Association https://calypsonet.org/
 *
 * See the NOTICE file(s) distributed with this work for additional information
 * regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 ************************************************************************************** */
package org.eclipse.keyple.distributed;

/**
 * This POJO contains data exchanged between <b>Local</b> and <b>Remote</b> components.
 *
 * <p>It is built and processed by the main distributed components, and you don't have to modify it.
 *
 * <p>You only need to transfer it via the network by serializing and deserializing it on your own.
 *
 * <p>However, it is necessary in some contexts to access certain information such as the
 * <b>sessionId</b> in the case of asynchronous communication or the <b>serverNodeId</b> in the case
 * of synchronous communication with several server instances.
 *
 * <p>Note that you can extend it or encapsulate it in another object if you need to transport other
 * technical information related to the network infrastructure for example.
 *
 * @since 2.0.0
 */
public class MessageDto {

  /**
   * The API level of the Distributed layer: {@value}
   *
   * @since 2.3.0
   */
  static final int API_LEVEL = 2;

  private int apiLevel;
  private String sessionId;
  private String action;
  private String clientNodeId;
  private String serverNodeId;
  private String localReaderName;
  private String remoteReaderName;
  private String body;

  /**
   * Enumeration of all possible actions.
   *
   * @since 2.0.0
   */
  enum Action {

    /**
     * Executes a Keyple plugin/reader service remotely.
     *
     * @since 2.0.0
     */
    CMD,

    /**
     * Contains the result of a Keyple plugin/reader service execution.
     *
     * @since 2.0.0
     */
    RESP,

    /**
     * Message containing a plugin event.
     *
     * @since 2.0.0
     */
    PLUGIN_EVENT,

    /**
     * Message containing a reader event.
     *
     * @since 2.0.0
     */
    READER_EVENT,

    /**
     * Checks for plugin event.<br>
     * Used for "Reader Server Side" use case with "synchronous" network protocol.
     *
     * @since 2.0.0
     */
    CHECK_PLUGIN_EVENT,

    /**
     * Checks for reader event.<br>
     * Used for "Reader Server Side" use case with "synchronous" network protocol.
     *
     * @since 2.0.0
     */
    CHECK_READER_EVENT,

    /**
     * Executes a ticketing application service remotely.<br>
     * Used for "Reader Client Side" use case.
     *
     * @since 2.0.0
     */
    EXECUTE_REMOTE_SERVICE,

    /**
     * Ends a remote ticketing application service.<br>
     * Used for "Reader Client Side" use case.
     *
     * @since 2.0.0
     */
    END_REMOTE_SERVICE,

    /**
     * Message containing an error.
     *
     * @since 2.0.0
     */
    ERROR
  }

  /**
   * Enumeration of all available common JSON properties.
   *
   * @since 2.0.0
   */
  enum JsonProperty {

    /**
     * @since 2.3.0
     */
    CORE_API_LEVEL("coreApiLevel"),

    /**
     * @since 2.0.0
     */
    INITIAL_CARD_CONTENT("initialCardContent"),

    /**
     * @since 2.0.0
     */
    INITIAL_CARD_CONTENT_CLASS_NAME("initialCardContentClassName"),

    /**
     * @since 2.0.0
     */
    INPUT_DATA("inputData"),

    /**
     * @since 2.0.0
     */
    OUTPUT_DATA("outputData"),

    /**
     * @since 2.0.0
     */
    SERVICE_ID("serviceId"),

    /**
     * @since 2.0.0
     */
    STRATEGY("strategy"),

    /**
     * @since 2.0.0
     */
    DURATION("duration");

    private final String key;

    /**
     * @param key The key of the JSON property.
     * @since 2.0.1
     */
    JsonProperty(String key) {
      this.key = key;
    }

    /**
     * @return The key of the JSON property.
     * @since 2.0.1
     */
    public String getKey() {
      return key;
    }
  }

  /**
   * Constructor.
   *
   * @since 2.0.0
   */
  public MessageDto() {
    apiLevel = API_LEVEL;
  }

  /**
   * Constructor by copy.
   *
   * @param from The source dto to copy.
   * @since 2.0.0
   */
  public MessageDto(MessageDto from) {
    apiLevel = from.getApiLevel();
    sessionId = from.getSessionId();
    action = from.getAction();
    clientNodeId = from.getClientNodeId();
    serverNodeId = from.getServerNodeId();
    localReaderName = from.getLocalReaderName();
    remoteReaderName = from.getRemoteReaderName();
    body = from.getBody();
  }

  /**
   * Returns the API level or 0 if the message is received from a system using a version of this
   * library lower than {@code 2.3.0}
   *
   * @return A positive value or 0 if the message is received from a system using a version of this
   *     library lower than {@code 2.3.0}
   * @since 2.3.0
   */
  public final int getApiLevel() {
    return apiLevel;
  }

  /**
   * This setter method must only be used during the deserialization process.
   *
   * @param apiLevel The API level to set.
   * @return the object instance.
   * @since 2.3.0
   */
  public final MessageDto setApiLevel(int apiLevel) {
    this.apiLevel = apiLevel;
    return this;
  }

  /**
   * Returns the session id.<br>
   * In case of a full duplex communication, this field will permit to client and server to identify
   * the socket.<br>
   * This id is also useful for debugging.
   *
   * @return a not empty string.
   * @since 2.0.0
   */
  public final String getSessionId() {
    return sessionId;
  }

  /**
   * This setter method must only be used during the deserialization process.
   *
   * @param sessionId The session id to set.
   * @return the object instance.
   * @since 2.0.0
   */
  public final MessageDto setSessionId(String sessionId) {
    this.sessionId = sessionId;
    return this;
  }

  /**
   * Returns the name of the internal action to perform in case of a request, or the original action
   * performed in case of a response.
   *
   * @return a not empty string.
   * @since 2.0.0
   */
  public final String getAction() {
    return action;
  }

  /**
   * This setter method must only be used during the deserialization process.
   *
   * @param action The action to set.
   * @return the object instance.
   * @since 2.0.0
   */
  public final MessageDto setAction(String action) {
    this.action = action;
    return this;
  }

  /**
   * Returns the client node id.
   *
   * @return a not empty string.
   * @since 2.0.0
   */
  public final String getClientNodeId() {
    return clientNodeId;
  }

  /**
   * This setter method must only be used during the deserialization process.
   *
   * @param clientNodeId The client node id to set.
   * @return the object instance.
   * @since 2.0.0
   */
  public final MessageDto setClientNodeId(String clientNodeId) {
    this.clientNodeId = clientNodeId;
    return this;
  }

  /**
   * Returns the server node id.<br>
   * In case of a multi-servers environment, this field will permit to client or load balancer to
   * identify the target server to access.
   *
   * @return a null string in case of the first transaction call.
   * @since 2.0.0
   */
  public final String getServerNodeId() {
    return serverNodeId;
  }

  /**
   * This setter method must only be used during the deserialization process.
   *
   * @param serverNodeId The server node id to set.
   * @return the object instance.
   * @since 2.0.0
   */
  public final MessageDto setServerNodeId(String serverNodeId) {
    this.serverNodeId = serverNodeId;
    return this;
  }

  /**
   * Returns the name of the local reader name associated to the transaction.
   *
   * @return a null string in case of a discovering readers call.
   * @since 2.0.0
   */
  public final String getLocalReaderName() {
    return localReaderName;
  }

  /**
   * This setter method must only be used during the deserialization process.
   *
   * @param localReaderName The local reader name to set.
   * @return the object instance.
   * @since 2.0.0
   */
  public final MessageDto setLocalReaderName(String localReaderName) {
    this.localReaderName = localReaderName;
    return this;
  }

  /**
   * Returns the name of the remote reader associated to the transaction.
   *
   * @return a null string in case of a discovering readers call.
   * @since 2.0.0
   */
  public final String getRemoteReaderName() {
    return remoteReaderName;
  }

  /**
   * This setter method must only be used during the deserialization process.
   *
   * @param remoteReaderName The remote reader name to set.
   * @return the object instance.
   * @since 2.0.0
   */
  public final MessageDto setRemoteReaderName(String remoteReaderName) {
    this.remoteReaderName = remoteReaderName;
    return this;
  }

  /**
   * Returns the body content.
   *
   * @return a null string in case of an error message.
   * @since 2.0.0
   */
  public final String getBody() {
    return body;
  }

  /**
   * This setter method must only be used during the deserialization process.
   *
   * @param body The body to set.
   * @return the object instance.
   * @since 2.0.0
   */
  public final MessageDto setBody(String body) {
    this.body = body;
    return this;
  }
}
