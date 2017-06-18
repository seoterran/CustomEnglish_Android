/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2015-08-03 17:34:38 UTC)
 * on 2015-09-21 at 15:56:49 UTC 
 * Modify at your own risk.
 */

package com.appspot.englishsharing_1041.helloworld.model;

/**
 * Model definition for HelloVerbRequest.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the helloworld. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class HelloVerbRequest extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long level;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String verb;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("verb_id") @com.google.api.client.json.JsonString
  private java.lang.Long verbId;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getLevel() {
    return level;
  }

  /**
   * @param level level or {@code null} for none
   */
  public HelloVerbRequest setLevel(java.lang.Long level) {
    this.level = level;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getVerb() {
    return verb;
  }

  /**
   * @param verb verb or {@code null} for none
   */
  public HelloVerbRequest setVerb(java.lang.String verb) {
    this.verb = verb;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getVerbId() {
    return verbId;
  }

  /**
   * @param verbId verbId or {@code null} for none
   */
  public HelloVerbRequest setVerbId(java.lang.Long verbId) {
    this.verbId = verbId;
    return this;
  }

  @Override
  public HelloVerbRequest set(String fieldName, Object value) {
    return (HelloVerbRequest) super.set(fieldName, value);
  }

  @Override
  public HelloVerbRequest clone() {
    return (HelloVerbRequest) super.clone();
  }

}
