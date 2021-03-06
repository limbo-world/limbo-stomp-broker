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

package org.limbo.stomp.server.starter;

import org.limbo.stomp.server.protocol.server.StompServer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Brozen
 * @since 2021-04-06
 */
public class StompServerStarter implements InitializingBean, DisposableBean {

    private final StompServer server;

    public StompServerStarter(StompServer server) {
        this.server = server;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // Bean初始化后启动服务
        server.start().sync();
    }

    @Override
    public void destroy() throws Exception {
        // Bean销毁时停止STOMP服务
        server.close().sync();
    }
}
