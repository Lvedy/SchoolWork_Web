-- 修改订单表结构的SQL语句
-- 适用于SQL Server数据库

-- 如果订单表不存在，先创建基础表结构
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='orders' AND xtype='U')
BEGIN
    CREATE TABLE [dbo].[orders] (
        [order_id] BIGINT IDENTITY(1,1) PRIMARY KEY,           -- 订单ID，自增主键
        [user_id] BIGINT NOT NULL,                            -- 下单用户ID
        [product_id] BIGINT NOT NULL,                         -- 商品ID
        [user_name] NVARCHAR(100) NOT NULL,                   -- 用户名称
        [product_name] NVARCHAR(200) NOT NULL,                -- 商品名称
        [product_quantity] INT NOT NULL DEFAULT 1,            -- 商品数量，默认为1
        [order_price] DECIMAL(10,2) NOT NULL,                 -- 订单价格，保留2位小数
        [is_completed] BIT DEFAULT 0,                         -- 订单是否完成：0-未完成，1-已完成
        [create_time] DATETIME2 DEFAULT GETDATE(),            -- 创建时间
        [update_time] DATETIME2 DEFAULT GETDATE()             -- 更新时间
    );
END
ELSE
BEGIN
    -- 如果表已存在，检查并添加缺失的字段
    
    -- 检查并添加订单是否完成字段
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
                   WHERE TABLE_NAME = 'orders' AND COLUMN_NAME = 'is_completed')
    BEGIN
        ALTER TABLE [dbo].[orders] 
        ADD [is_completed] BIT DEFAULT 0;
        
        -- 添加字段注释
        EXEC sp_addextendedproperty 
            @name = N'MS_Description', 
            @value = N'订单是否完成：0-未完成，1-已完成', 
            @level0type = N'SCHEMA', @level0name = N'dbo', 
            @level1type = N'TABLE', @level1name = N'orders', 
            @level2type = N'COLUMN', @level2name = N'is_completed';
    END
    
    -- 检查并添加用户名称字段
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
                   WHERE TABLE_NAME = 'orders' AND COLUMN_NAME = 'user_name')
    BEGIN
        ALTER TABLE [dbo].[orders] 
        ADD [user_name] NVARCHAR(100) NOT NULL DEFAULT '';
        
        -- 添加字段注释
        EXEC sp_addextendedproperty 
            @name = N'MS_Description', 
            @value = N'用户名称', 
            @level0type = N'SCHEMA', @level0name = N'dbo', 
            @level1type = N'TABLE', @level1name = N'orders', 
            @level2type = N'COLUMN', @level2name = N'user_name';
    END
    
    -- 检查并添加商品名称字段
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
                   WHERE TABLE_NAME = 'orders' AND COLUMN_NAME = 'product_name')
    BEGIN
        ALTER TABLE [dbo].[orders] 
        ADD [product_name] NVARCHAR(200) NOT NULL DEFAULT '';
        
        -- 添加字段注释
        EXEC sp_addextendedproperty 
            @name = N'MS_Description', 
            @value = N'商品名称', 
            @level0type = N'SCHEMA', @level0name = N'dbo', 
            @level1type = N'TABLE', @level1name = N'orders', 
            @level2type = N'COLUMN', @level2name = N'product_name';
    END
    
    -- 检查并修改商品数量字段（如果存在但类型不对）
    IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'orders' AND COLUMN_NAME = 'product_quantity')
    BEGIN
        -- 如果字段存在但类型不是INT，则修改类型
        IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
                       WHERE TABLE_NAME = 'orders' AND COLUMN_NAME = 'product_quantity' 
                       AND DATA_TYPE = 'int')
        BEGIN
            ALTER TABLE [dbo].[orders] 
            ALTER COLUMN [product_quantity] INT NOT NULL;
        END
    END
    ELSE
    BEGIN
        -- 如果字段不存在，则添加
        ALTER TABLE [dbo].[orders] 
        ADD [product_quantity] INT NOT NULL DEFAULT 1;
        
        -- 添加字段注释
        EXEC sp_addextendedproperty 
            @name = N'MS_Description', 
            @value = N'商品数量', 
            @level0type = N'SCHEMA', @level0name = N'dbo', 
            @level1type = N'TABLE', @level1name = N'orders', 
            @level2type = N'COLUMN', @level2name = N'product_quantity';
    END
    
    -- 检查并修改订单价格字段（如果存在但类型不对）
    IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'orders' AND COLUMN_NAME = 'order_price')
    BEGIN
        -- 如果字段存在但类型不是DECIMAL(10,2)，则修改类型
        IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
                       WHERE TABLE_NAME = 'orders' AND COLUMN_NAME = 'order_price' 
                       AND DATA_TYPE = 'decimal' AND NUMERIC_PRECISION = 10 AND NUMERIC_SCALE = 2)
        BEGIN
            ALTER TABLE [dbo].[orders] 
            ALTER COLUMN [order_price] DECIMAL(10,2) NOT NULL;
        END
    END
    ELSE
    BEGIN
        -- 如果字段不存在，则添加
        ALTER TABLE [dbo].[orders] 
        ADD [order_price] DECIMAL(10,2) NOT NULL DEFAULT 0.00;
        
        -- 添加字段注释
        EXEC sp_addextendedproperty 
            @name = N'MS_Description', 
            @value = N'订单价格', 
            @level0type = N'SCHEMA', @level0name = N'dbo', 
            @level1type = N'TABLE', @level1name = N'orders', 
            @level2type = N'COLUMN', @level2name = N'order_price';
    END
END

-- 创建或更新索引以提高查询性能
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_orders_user_id')
    CREATE INDEX IX_orders_user_id ON [dbo].[orders] ([user_id]);

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_orders_product_id')
    CREATE INDEX IX_orders_product_id ON [dbo].[orders] ([product_id]);

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_orders_is_completed')
    CREATE INDEX IX_orders_is_completed ON [dbo].[orders] ([is_completed]);

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_orders_create_time')
    CREATE INDEX IX_orders_create_time ON [dbo].[orders] ([create_time]);

-- 添加表注释（如果不存在）
IF NOT EXISTS (SELECT * FROM sys.extended_properties 
               WHERE major_id = OBJECT_ID('dbo.orders') AND minor_id = 0 AND name = 'MS_Description')
BEGIN
    EXEC sp_addextendedproperty 
        @name = N'MS_Description', 
        @value = N'订单表，存储用户订单信息', 
        @level0type = N'SCHEMA', @level0name = N'dbo', 
        @level1type = N'TABLE', @level1name = N'orders';
END

PRINT '订单表结构修改完成！';
PRINT '表包含字段：订单ID、下单用户ID、商品ID、用户名称、商品名称、商品数量、订单价格、订单是否完成';