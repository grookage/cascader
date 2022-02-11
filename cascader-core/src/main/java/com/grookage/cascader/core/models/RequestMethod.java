/*
 * Copyright 2020 Koushik R <rkoushik.14@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.grookage.cascader.core.models;

@SuppressWarnings("UnusedReturnValue")
public enum RequestMethod {

    HEAD{
        public <T> T accept(RequestMethod.RequestMethodVisitor<T> visitor) {
            return visitor.visitHead();
        }
    },

    GET{
        public <T> T accept(RequestMethod.RequestMethodVisitor<T> visitor) {
            return visitor.visitGet();
        }
    },

    POST{
        public <T> T accept(RequestMethod.RequestMethodVisitor<T> visitor) {
            return visitor.visitPost();
        }
    },

    PUT{
        public <T> T accept(RequestMethod.RequestMethodVisitor<T> visitor) {
            return visitor.visitPut();
        }
    },

    OPTIONS{
        public <T> T accept(RequestMethod.RequestMethodVisitor<T> visitor) {
            return visitor.visitOptions();
        }
    },

    PATCH{
        public <T> T accept(RequestMethod.RequestMethodVisitor<T> visitor) {
            return visitor.visitPatch();
        }
    },

    DELETE{
        public <T> T accept(RequestMethod.RequestMethodVisitor<T> visitor) {
            return visitor.visitDelete();
        }
    };

    RequestMethod() {

    }

    public abstract <T> T accept(RequestMethod.RequestMethodVisitor<T> visitor);

    public interface RequestMethodVisitor<T> {
        T visitHead();

        T visitGet();

        T visitPost();

        T visitPut();

        T visitOptions();

        T visitPatch();

        T visitDelete();
    }
}

