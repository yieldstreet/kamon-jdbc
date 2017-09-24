/* =========================================================================================
 * Copyright © 2013-2017 the kamon project <http://kamon.io/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * =========================================================================================
 */

package kamon.jdbc.instrumentation.maria

import java.sql.{ResultSet, SQLException}
import java.util.concurrent.Callable

import kamon.agent.libs.net.bytebuddy.implementation.bind.annotation.{RuntimeType, SuperCall, This}
import kamon.jdbc.instrumentation.bridge.MariaPreparedStatement
import org.mariadb.jdbc.internal.queryresults.resultset.MariaSelectResultSet

/**
  * Interceptor for org.mariadb.jdbc.MariaDbServerPreparedStatement::executeQuery
  */
object ExecuteQueryMethodInterceptor {

  @RuntimeType
  @throws(classOf[SQLException])
  def executeQuery(@SuperCall callable: Callable[_], @This preparedStatement:Object): ResultSet = {
    val pps = preparedStatement.asInstanceOf[MariaPreparedStatement]

    if(pps.kamon$executeInternal(pps.kamon$getFetchSize(), canHaveResultset = false))
      pps.kamon$getResultSet()
    else
      MariaSelectResultSet.createEmptyResultSet
  }
}

/**
  * Interceptor for org.mariadb.jdbc.MariaDbServerPreparedStatement::executeUpdate
  */
object ExecuteUpdateMethodInterceptor {

  @RuntimeType
  @throws(classOf[SQLException])
  def executeUpdate(@SuperCall callable: Callable[_], @This preparedStatement:Object): Int = {
    val pps = preparedStatement.asInstanceOf[MariaPreparedStatement]
    pps.kamon$executeInternal(pps.kamon$getFetchSize(), canHaveResultset = false)
    pps.kamon$getUpdateCount()
  }
}

