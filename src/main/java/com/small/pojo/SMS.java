package com.small.pojo;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息对象实体
 * Created by Administrator on 2018/6/21.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SMS {

    private String smsContent;

    private String reciver;

    private String failCount;

}
