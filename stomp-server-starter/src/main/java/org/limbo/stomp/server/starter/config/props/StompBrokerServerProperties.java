/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.limbo.stomp.server.starter.config.props;

import lombok.Data;

/**
 * @author Brozen
 * @since 2021-04-06
 */
@Data
public class StompBrokerServerProperties {

    /**
     * STOMP 服务器绑定的端口
     */
    private Integer port;

    /**
     * 连接加入处理线程池大小
     */
    private Integer acceptorCount = 2;

    /**
     * 数据包处理线程池大小
     */
    private Integer workerCount = 4;

}
