package com.naatsms.payment.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("merchant")
public class Merchant{

    @Id
    @Column("merchantId")
    Long merchantId;
    @Column("merchantName") String name;
    @Column("secretKey") String secretKey;

    public Long getMerchantId()
    {
        return merchantId;
    }

    public void setMerchantId(final Long id)
    {
        this.merchantId = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getSecretKey()
    {
        return secretKey;
    }

    public void setSecretKey(final String secretKey)
    {
        this.secretKey = secretKey;
    }
}

