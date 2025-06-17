// 全局变量
let currentUser = null;
let userToken = null;
let orders = [];

let products = [];

// 渲染产品图片
function renderProductImage(imagePath, isDetailPage = false) {
    if (!imagePath || imagePath === '无') {
        return '暂无图片';
    }
    
    // 如果是路径，创建img标签
    if (imagePath.includes('/') || imagePath.includes('\\') || imagePath.includes('.')) {
        // 修正图片路径，确保相对于当前HTML文件的正确路径
        let correctedPath = imagePath;
        
        // 如果路径不是以../开头，添加../前缀
        if (!imagePath.startsWith('../') && !imagePath.startsWith('http')) {
            // 移除可能的前导斜杠
            correctedPath = imagePath.replace(/^[\/\\]+/, '');
            // 添加正确的相对路径前缀
            correctedPath = '../' + correctedPath;
        }
        
        const imgStyle = isDetailPage ? 'width: 200px; height: 200px; object-fit: cover; border-radius: 10px;' : 'width: 100%; height: 150px; object-fit: cover; border-radius: 8px;';
        return `<img src="${correctedPath}" alt="产品图片" style="${imgStyle}" onerror="this.style.display='none'; this.parentNode.innerHTML='暂无图片';">`;
    }
    
    // 如果是emoji或其他文本，直接返回
    return imagePath;
}

// 分页相关变量
let currentPage = 1;
let pageSize = 6;
let totalPages = 1;
let totalRecords = 0;

// 加载产品列表
function loadProductList(page = 1) {
    const token = localStorage.getItem('userToken');
    if (!token) {
        showMessage('请先登录', 'error');
        return;
    }

    currentPage = page;
    
    // 向网关发送获取产品列表请求
    fetch(`http://hbe.vanmc.cn:19198/api-service-product/api/app/product/list/${page}/${pageSize}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'token': token
        },
        body: JSON.stringify({})
    })
    .then(response => response.json())
    .then(data => {
        if (data.success && data.code === 1) {
            const productData = data.data;
            products = productData.records || [];
            totalRecords = productData.total || 0;
            totalPages = Math.ceil(totalRecords / pageSize);
            
            // 渲染产品列表
            renderProductList(products);
            
            // 渲染分页控件
            renderPagination();
        } else {
            showMessage(data.msg || '获取产品列表失败', 'error');
            if (data.code === 203) {
                // token失效，跳转到登录页
                localStorage.removeItem('userToken');
                window.location.href = 'login.html';
            }
        }
    })
    .catch(error => {
        console.error('获取产品列表失败:', error);
        showMessage('网络错误，获取产品列表失败', 'error');
    });
}

// 渲染产品列表
function renderProductList(productList) {
    const productGrid = document.querySelector('#product-list .product-grid');
    if (!productGrid) return;
    
    if (productList.length === 0) {
        productGrid.innerHTML = '<div class="no-data">暂无产品数据</div>';
        return;
    }
    
    productGrid.innerHTML = productList.map(product => `
        <div class="product-card">
            <div class="product-image">${renderProductImage(product.productImg)}</div>
            <h3>${product.productName || '未知产品'}</h3>
            <p class="price">¥${product.price || 0}</p>
            <button class="btn-primary" onclick="viewProduct('${product.id}')">查看详情</button>
        </div>
    `).join('');
    
    // 更新产品选择下拉框
    updateProductSelect(products);
}

// 更新产品选择下拉框
function updateProductSelect(products) {
    const productSelect = document.getElementById('productSelect');
    if (!productSelect) return;
    
    // 清空现有选项，保留默认选项
    productSelect.innerHTML = '<option value="">请选择产品</option>';
    
    // 添加产品选项
    products.forEach(product => {
        const option = document.createElement('option');
        option.value = product.id.toString();
        option.textContent = `${product.productName} - ¥${product.price}`;
        productSelect.appendChild(option);
    });
}

// 渲染分页控件
function renderPagination() {
    const paginationContainer = document.querySelector('.pagination-container');
    if (!paginationContainer) return;
    
    if (totalPages <= 1) {
        paginationContainer.innerHTML = '';
        return;
    }
    
    let paginationHTML = '<div class="pagination">';
    
    // 上一页按钮
    if (currentPage > 1) {
        paginationHTML += `<button class="pagination-btn" onclick="loadProductList(${currentPage - 1})">上一页</button>`;
    } else {
        paginationHTML += '<button class="pagination-btn disabled">上一页</button>';
    }
    
    // 页码按钮
    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, currentPage + 2);
    
    if (startPage > 1) {
        paginationHTML += '<button class="pagination-btn" onclick="loadProductList(1)">1</button>';
        if (startPage > 2) {
            paginationHTML += '<span class="pagination-ellipsis">...</span>';
        }
    }
    
    for (let i = startPage; i <= endPage; i++) {
        if (i === currentPage) {
            paginationHTML += `<button class="pagination-btn active">${i}</button>`;
        } else {
            paginationHTML += `<button class="pagination-btn" onclick="loadProductList(${i})">${i}</button>`;
        }
    }
    
    if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
            paginationHTML += '<span class="pagination-ellipsis">...</span>';
        }
        paginationHTML += `<button class="pagination-btn" onclick="loadProductList(${totalPages})">${totalPages}</button>`;
    }
    
    // 下一页按钮
    if (currentPage < totalPages) {
        paginationHTML += `<button class="pagination-btn" onclick="loadProductList(${currentPage + 1})">下一页</button>`;
    } else {
        paginationHTML += '<button class="pagination-btn disabled">下一页</button>';
    }
    
    // 跳转到指定页面
    paginationHTML += `
        <div class="pagination-jump">
            <span>跳转到</span>
            <input type="number" id="jumpToPage" min="1" max="${totalPages}" value="${currentPage}" class="pagination-input">
            <span>页</span>
            <button class="pagination-btn" onclick="jumpToPage()">确定</button>
        </div>
    `;
    
    // 分页信息
    paginationHTML += `
        <div class="pagination-info">
            <span>共 ${totalRecords} 条记录，第 ${currentPage}/${totalPages} 页</span>
        </div>
    `;
    
    paginationHTML += '</div>';
    
    paginationContainer.innerHTML = paginationHTML;
}

// 跳转到指定页面
function jumpToPage() {
    const jumpInput = document.getElementById('jumpToPage');
    if (!jumpInput) return;
    
    const targetPage = parseInt(jumpInput.value);
    if (targetPage >= 1 && targetPage <= totalPages && targetPage !== currentPage) {
        loadProductList(targetPage);
    }
}

// 搜索产品（支持多个条件）
function searchProducts() {
    const productId = document.getElementById('productSearchId').value.trim();
    const productName = document.getElementById('productSearchName').value.trim();
    const startPrice = document.getElementById('startPrice').value.trim();
    const endPrice = document.getElementById('endPrice').value.trim();
    
    // 检查是否至少有一个搜索条件
    if (!productId && !productName && !startPrice && !endPrice) {
        showMessage('请至少输入一个搜索条件', 'error');
        return;
    }
    
    // 验证价格范围
    if (startPrice && endPrice && parseFloat(startPrice) > parseFloat(endPrice)) {
        showMessage('起始价格不能大于终止价格', 'error');
        return;
    }
    
    const token = localStorage.getItem('userToken');
    if (!token) {
        showMessage('请先登录', 'error');
        return;
    }
    
    // 构建搜索参数
    const searchParams = {};
    if (productId) searchParams.id = productId;
    if (productName) searchParams.productName = productName;
    if (startPrice) searchParams.startPrice = parseFloat(startPrice);
    if (endPrice) searchParams.endPrice = parseFloat(endPrice);
    
    // 调用产品列表接口进行搜索
    fetch(`http://hbe.vanmc.cn:19198/api-service-product/api/app/product/list/1/${pageSize}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'token': token
        },
        body: JSON.stringify(searchParams)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success && data.code === 1) {
            const productData = data.data;
            const searchResults = productData.records || [];
            
            // 使用共用的渲染方法显示搜索结果
            renderProductList(searchResults);
            
            // 隐藏分页控件（搜索结果不分页）
            const paginationContainer = document.querySelector('.pagination-container');
            if (paginationContainer) {
                paginationContainer.style.display = 'none';
            }
            
            showMessage(`搜索完成，找到 ${searchResults.length} 个产品`, 'success');
        } else {
            showMessage(data.msg || '搜索失败', 'error');
            // 显示无结果
            renderProductList([]);
        }
    })
    .catch(error => {
        console.error('搜索产品失败:', error);
        showMessage('网络错误，搜索失败', 'error');
        renderProductList([]);
    });
}



// 清除搜索
function clearSearch() {
    // 清空所有搜索输入框
    const productIdInput = document.getElementById('productSearchId');
    const productNameInput = document.getElementById('productSearchName');
    const startPriceInput = document.getElementById('startPrice');
    const endPriceInput = document.getElementById('endPrice');
    
    if (productIdInput) productIdInput.value = '';
    if (productNameInput) productNameInput.value = '';
    if (startPriceInput) startPriceInput.value = '';
    if (endPriceInput) endPriceInput.value = '';
    
    // 重新加载产品列表
    loadProductList(1);
    
    // 显示分页控件
    const paginationContainer = document.querySelector('.pagination-container');
    if (paginationContainer) {
        paginationContainer.style.display = 'flex';
    }
    
    showMessage('已清除搜索', 'success');
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    initializePage();
    setupEventListeners();
    checkUserAuthentication();
});

// 初始化页面
function initializePage() {
    // 设置默认显示的模块
    showModule('profile');
    
    // 初始化用户信息
    loadUserInfo();
    
    // 加载产品列表
    loadProductList();
    
    // 初始化订单列表
loadOrderList();
}

// 设置事件监听器
function setupEventListeners() {
    // 导航菜单点击事件
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.addEventListener('click', function() {
            const module = this.getAttribute('data-module');
            switchModule(module);
        });
    });
    
    // 标签页切换事件
    const tabBtns = document.querySelectorAll('.tab-btn');
    tabBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const tab = this.getAttribute('data-tab');
            switchTab(tab, this);
        });
    });
    
    // 退出登录按钮
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    }
    
    // 订单表单提交
    const orderForm = document.getElementById('orderForm');
    if (orderForm) {
        orderForm.addEventListener('submit', handleOrderSubmit);
    }
}

// 检查用户认证状态
function checkUserAuthentication() {
    // 从localStorage获取token
    userToken = localStorage.getItem('userToken');
    const username = localStorage.getItem('username');
    
    if (!userToken || !username) {
        // 如果没有token，重定向到登录页面
        showMessage('请先登录', 'error');
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 2000);
        return;
    }
    
    // 设置当前用户信息
    currentUser = {
        username: username,
        token: userToken,
        joinDate: localStorage.getItem('joinDate') || '2024-01-01'
    };
    
    // 更新欢迎信息
    updateWelcomeText();
}

// 更新欢迎文本
function updateWelcomeText() {
    const welcomeText = document.getElementById('welcomeText');
    if (welcomeText && currentUser) {
        welcomeText.textContent = `欢迎回来，${currentUser.username}！`;
    }
}

// 加载用户信息
function loadUserInfo() {
    const token = localStorage.getItem('userToken');
    if (!token) {
        showMessage('请先登录', 'error');
        window.location.href = 'login.html';
        return;
    }

    // 向网关发送获取用户信息请求
    fetch('http://hbe.vanmc.cn:19198/api-service-user/api/app/user/getInfo', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'token': token
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success && data.code === 1) {
            const userInfo = data.data;
            
            // 更新页面显示
            const userNameElement = document.getElementById('userName');
            const userRoleElement = document.getElementById('userRole');
            const joinDateElement = document.getElementById('joinDate');
            
            if (userNameElement) {
                userNameElement.textContent = userInfo.nickname || userInfo.username || '用户';
            }
            if (userRoleElement) {
                userRoleElement.textContent = '用户ID: ' + userInfo.id;
            }
            if (joinDateElement) {
                const createTime = userInfo.createTime;
                if (createTime) {
                    const date = new Date(createTime);
                    joinDateElement.textContent = date.toLocaleDateString();
                } else {
                    joinDateElement.textContent = '未知';
                }
            }
            
            // 保存用户信息到本地存储
            localStorage.setItem('userInfo', JSON.stringify(userInfo));
        } else {
            showMessage(data.msg || '获取用户信息失败', 'error');
            if (data.code === 203) {
                // token失效，跳转到登录页
                localStorage.removeItem('userToken');
                window.location.href = 'login.html';
            }
        }
    })
    .catch(error => {
        console.error('获取用户信息失败:', error);
        showMessage('网络错误，获取用户信息失败', 'error');
    });
}

// 模块切换
function switchModule(moduleName) {
    // 隐藏所有模块
    const modules = document.querySelectorAll('.module');
    modules.forEach(module => {
        module.classList.remove('active');
    });
    
    // 显示指定模块
    const targetModule = document.getElementById(moduleName + '-module');
    if (targetModule) {
        targetModule.classList.add('active');
    }
    
    // 更新导航状态
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.classList.remove('active');
        if (item.getAttribute('data-module') === moduleName) {
            item.classList.add('active');
        }
    });
    
    // 模块特定的初始化逻辑
    if (moduleName === 'products') {
        loadProductList();
    }
}

// 显示模块
function showModule(moduleName) {
    switchModule(moduleName);
}

// 标签页切换
function switchTab(tabName, clickedBtn) {
    // 获取父容器
    const moduleContent = clickedBtn.closest('.module').querySelector('.module-content');
    
    // 隐藏所有标签内容
    const tabContents = moduleContent.querySelectorAll('.tab-content');
    tabContents.forEach(content => {
        content.classList.remove('active');
    });
    
    // 显示指定标签内容
    const targetTab = moduleContent.querySelector('#' + tabName);
    if (targetTab) {
        targetTab.classList.add('active');
    }
    
    // 更新标签按钮状态
    const tabBtns = clickedBtn.parentElement.querySelectorAll('.tab-btn');
    tabBtns.forEach(btn => {
        btn.classList.remove('active');
    });
    clickedBtn.classList.add('active');
}

// 查看产品详情
function viewProduct(productId) {
    const token = localStorage.getItem('userToken');
    if (!token) {
        showMessage('请先登录', 'error');
        return;
    }

    // 向网关发送获取产品详情请求
    fetch('http://hbe.vanmc.cn:19198/api-service-product/api/app/product/queryOne', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'token': token
        },
        body: JSON.stringify({ id: productId })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success && data.code === 1) {
            const product = data.data;
            
            // 渲染产品详情
            renderProductDetail(product);
            
            // 切换到产品详情标签
            const productModule = document.getElementById('products-module');
            const detailTab = productModule.querySelector('[data-tab="product-detail"]');
            switchTab('product-detail', detailTab);
        } else {
            showMessage(data.msg || '获取产品详情失败', 'error');
        }
    })
    .catch(error => {
        console.error('获取产品详情失败:', error);
        showMessage('网络错误，获取产品详情失败', 'error');
    });
}

// 渲染产品详情
function renderProductDetail(product) {
    const detailContent = document.getElementById('product-detail');
    if (!detailContent) return;
    
    detailContent.innerHTML = `
        <div class="product-detail-card">
            <div class="product-image" style="font-size: 4rem; margin-bottom: 1rem;">${renderProductImage(product.productImg, true)}</div>
            <h3>${product.productName || '未知产品'}</h3>
            <p class="price" style="font-size: 1.5rem; margin: 1rem 0;">¥${product.price || 0}</p>
            <p style="margin-bottom: 2rem; line-height: 1.6;">${product.productDesc || '暂无描述'}</p>
            <div class="product-info">
                <p><strong>产品ID:</strong> ${product.id}</p>
                <p><strong>创建时间:</strong> ${product.createTime ? new Date(product.createTime).toLocaleString() : '未知'}</p>
                <p><strong>状态:</strong> ${product.enableFlag ? '启用' : '禁用'}</p>
            </div>
            <button class="btn-primary" onclick="orderNow('${product.id}')">立即下单</button>
        </div>
    `;
}

// 立即下单
function orderNow(productId) {
    // 切换到订单模块
    switchModule('orders');
    
    // 切换到创建订单标签页
    const orderModule = document.getElementById('orders-module');
    const createTab = orderModule.querySelector('[data-tab="create-order"]');
    switchTab('create-order', createTab);
    
    // 自动填充产品ID
    setTimeout(() => {
        const productIdInput = document.getElementById('productId');
        if (productIdInput) {
            productIdInput.value = productId;
            productIdInput.focus();
        }
    }, 100);
}

// 处理订单提交
async function handleOrderSubmit(event) {
    event.preventDefault();
    
    const productIdInput = document.getElementById('productId');
    const quantity = document.getElementById('quantity');
    const address = document.getElementById('address');
    
    if (!productIdInput.value || !quantity.value) {
        showMessage('请填写产品ID和数量', 'error');
        return;
    }
    
    const productId = productIdInput.value.trim();
    
    try {
        const token = localStorage.getItem('userToken');
        if (!token) {
            showMessage('请先登录', 'error');
            return;
        }

        // 调用后端API创建订单
        const response = await fetch('http://hbe.vanmc.cn:19198/api-service-order/api/app/order/createOrder', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'token': token
            },
            body: JSON.stringify({
                productId: productId,
                quantity: parseInt(quantity.value),
                address: address.value || null
            })
        });

        const result = await response.json();
        
        if (result.code === 1) {
            // 清空表单
            event.target.reset();
            
            showMessage('订单创建成功！', 'success');
            
            // 重新加载订单列表
            loadOrderList();
            
            // 切换到订单列表
            const orderModule = document.getElementById('orders-module');
            const listTab = orderModule.querySelector('[data-tab="order-list"]');
            switchTab('order-list', listTab);
        } else {
            console.error('创建订单失败:', result.message);
            showMessage(result.message || '创建订单失败', 'error');
        }
    } catch (error) {
        console.error('创建订单出错:', error);
        showMessage('网络错误，请稍后重试', 'error');
    }
}

// 从后端加载订单列表
async function loadOrderList() {
    try {
        const token = localStorage.getItem('userToken');
        if (!token) {
            showMessage('请先登录', 'error');
            return;
        }

        const response = await fetch('http://hbe.vanmc.cn:19198/api-service-order/api/app/order/getOrderList', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'token': token
            }
        });

        const result = await response.json();

        if (result.code === 1 && result.data) {
            orders = result.data;
            renderOrderList();
        } else {
            console.error('获取订单列表失败:', result.message);
            showMessage(result.message || '获取订单列表失败', 'error');
        }
    } catch (error) {
        console.error('获取订单列表出错:', error);
        showMessage('网络错误，请稍后重试', 'error');
    }
}

// 渲染订单列表
function renderOrderList() {
    const orderList = document.querySelector('.order-list');
    if (!orderList) return;
    
    if (orders.length === 0) {
        orderList.innerHTML = '<div class="empty-state">暂无订单数据</div>';
        return;
    }
    
    orderList.innerHTML = orders.map(order => `
        <div class="order-item">
            <div class="order-info">
                <h4>订单ID:${order.id || order.orderId}</h4>
                <p>${order.product || order.productName} x${order.quantity}</p>
                <p>总价：¥${order.amount || order.totalPrice}</p>
                ${order.address ? `<p>地址：${order.address}</p>` : ''}
                ${order.createTime ? `<p>创建时间：${new Date(order.createTime).toLocaleString()}</p>` : ''}
                <p class="order-status ${order.status}">
                    ${getOrderStatusText(order.status)}
                </p>
            </div>
            <div class="order-actions">
                ${order.status === 'pending' || order.status === 0 ? 
                    `<button class="btn-success" onclick="completeOrder('${order.id || order.orderId}')">完成</button>` : 
                    ''
                }
                <button class="btn-danger" onclick="deleteOrder('${order.id || order.orderId}')">删除</button>
            </div>
        </div>
    `).join('');
}

// 获取订单状态文本
function getOrderStatusText(status) {
    switch (status) {
        case 'pending':
        case 0:
            return '待处理';
        case 'completed':
        case 1:
            return '已完成';
        case 'cancelled':
        case 2:
            return '已取消';
        default:
            return '未知状态';
    }
}

// 完成订单
async function completeOrder(orderId) {
    try {
        const token = localStorage.getItem('userToken');
        if (!token) {
            showMessage('请先登录', 'error');
            return;
        }

        const response = await fetch('http://hbe.vanmc.cn:19198/api-service-order/api/app/order/completeOrder', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'token': token
            },
            body: JSON.stringify({ orderId: orderId })
        });

        const result = await response.json();
        
        if (result.code === 200 || (result.success && result.code === 1)) {
            showMessage('订单已完成！', 'success');
            // 重新加载订单列表
            loadOrderList();
        } else {
            console.error('完成订单失败:', result.message || result.msg);
            showMessage(result.message || result.msg || '完成订单失败', 'error');
        }
    } catch (error) {
        console.error('完成订单出错:', error);
        showMessage('网络错误，请稍后重试', 'error');
    }
}

// 删除订单
async function deleteOrder(orderId) {
    if (!confirm('确定要删除这个订单吗？')) {
        return;
    }
    
    try {
        const token = localStorage.getItem('userToken');
        if (!token) {
            showMessage('请先登录', 'error');
            return;
        }

        const response = await fetch('http://hbe.vanmc.cn:19198/api-service-order/api/app/order/deleteOrder', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'token': token
            },
            body: JSON.stringify({ orderId: orderId })
        });

        const result = await response.json();
        
        if (result.code === 200 || (result.success && result.code === 1)) {
            showMessage('订单已删除', 'success');
            // 重新加载订单列表
            loadOrderList();
        } else {
            console.error('删除订单失败:', result.message || result.msg);
            showMessage(result.message || result.msg || '删除订单失败', 'error');
        }
    } catch (error) {
        console.error('删除订单出错:', error);
        showMessage('网络错误，请稍后重试', 'error');
    }
}

// 退出登录
function logout() {
    if (confirm('确定要退出登录吗？')) {
        const token = localStorage.getItem('userToken');
        
        if (!token) {
            // 如果没有token，直接跳转到登录页
            window.location.href = 'login.html';
            return;
        }
        
        // 向网关发送退出登录请求
        fetch('http://hbe.vanmc.cn:19198/api-service-user/api/app/user/logout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'token': token
            }
        })
        .then(response => response.json())
        .then(data => {
            // 无论服务器返回什么结果，都清除本地存储
            localStorage.removeItem('userToken');
            localStorage.removeItem('userInfo');
            localStorage.removeItem('username');
            localStorage.removeItem('joinDate');
            
            if (data.success && data.code === 1) {
                showMessage('已退出登录', 'success');
            } else {
                showMessage('退出登录完成', 'info');
            }
            
            // 延迟跳转到登录页面
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 1500);
        })
        .catch(error => {
            console.error('退出登录请求失败:', error);
            // 即使请求失败，也清除本地存储并跳转
            localStorage.removeItem('userToken');
            localStorage.removeItem('userInfo');
            localStorage.removeItem('username');
            localStorage.removeItem('joinDate');
            
            showMessage('已退出登录', 'success');
            
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 1500);
        });
    }
}

// 显示消息提示
function showMessage(message, type = 'success') {
    // 移除已存在的消息
    const existingMessage = document.querySelector('.message-popup');
    if (existingMessage) {
        existingMessage.remove();
    }
    
    // 创建消息元素
    const messageDiv = document.createElement('div');
    messageDiv.className = `message-popup ${type}`;
    messageDiv.innerHTML = `
        <div class="message-content">
            <span class="message-icon">
                ${type === 'success' ? '✅' : type === 'error' ? '❌' : 'ℹ️'}
            </span>
            <span class="message-text">${message}</span>
        </div>
    `;
    
    // 添加样式
    messageDiv.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${type === 'success' ? 'linear-gradient(135deg, #4CAF50, #45a049)' : 
                     type === 'error' ? 'linear-gradient(135deg, #f44336, #d32f2f)' : 
                     'linear-gradient(135deg, #2196F3, #1976D2)'};
        color: white;
        padding: 1rem 1.5rem;
        border-radius: 10px;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
        z-index: 10000;
        animation: slideIn 0.3s ease;
        max-width: 300px;
        word-wrap: break-word;
    `;
    
    // 添加动画样式
    const style = document.createElement('style');
    style.textContent = `
        @keyframes slideIn {
            from {
                transform: translateX(100%);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
        .message-content {
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        .message-icon {
            font-size: 1.2rem;
        }
        .message-text {
            font-weight: 500;
        }
    `;
    
    if (!document.querySelector('#message-styles')) {
        style.id = 'message-styles';
        document.head.appendChild(style);
    }
    
    // 添加到页面
    document.body.appendChild(messageDiv);
    
    // 3秒后自动移除
    setTimeout(() => {
        if (messageDiv.parentNode) {
            messageDiv.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => {
                messageDiv.remove();
            }, 300);
        }
    }, 3000);
    
    // 添加滑出动画
    if (!document.querySelector('#slideout-animation')) {
        const slideOutStyle = document.createElement('style');
        slideOutStyle.id = 'slideout-animation';
        slideOutStyle.textContent = `
            @keyframes slideOut {
                from {
                    transform: translateX(0);
                    opacity: 1;
                }
                to {
                    transform: translateX(100%);
                    opacity: 0;
                }
            }
        `;
        document.head.appendChild(slideOutStyle);
    }
}

// 工具函数：保存用户token
function saveUserToken(token, username) {
    localStorage.setItem('userToken', token);
    localStorage.setItem('username', username);
    localStorage.setItem('joinDate', new Date().toISOString().split('T')[0]);
}

// 工具函数：获取用户token
function getUserToken() {
    return localStorage.getItem('userToken');
}

// 工具函数：检查token是否有效
function isTokenValid() {
    const token = getUserToken();
    return token && token.length > 0;
}

// 显示功能不可用提示
function showNotAvailable() {
    showMessage('该功能永不上线', 'info');
}

// 获取推荐功能
async function getRecommendations() {
    try {
        const token = localStorage.getItem('userToken');
        if (!token) {
            showMessage('请先登录', 'error');
            return;
        }

        // 显示加载状态
        const contentElement = document.getElementById('guess-you-like-content');
        const originalContent = contentElement.textContent;
        contentElement.textContent = '正在获取推荐...';

        const response = await fetch('http://hbe.vanmc.cn:19198/api-service-recommend/api/app/recommend/refresh', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'token': token
            }
        });

        const result = await response.json();
        
        if (result.code === 200 || (result.success && result.code === 1)) {
            // 解析并显示推荐数据
            displayRecommendations(result.data);
            showMessage('推荐获取成功！', 'success');
        } else {
            console.error('获取推荐失败:', result.message || result.msg);
            showMessage(result.message || result.msg || '获取推荐失败', 'error');
            contentElement.textContent = originalContent;
        }
    } catch (error) {
        console.error('获取推荐出错:', error);
        showMessage('网络错误，请稍后重试', 'error');
        const contentElement = document.getElementById('guess-you-like-content');
        contentElement.textContent = '为您推荐个性化商品，点击获取推荐！';
    }
}

// 显示推荐数据
function displayRecommendations(recommendations) {
    const contentElement = document.getElementById('guess-you-like-content');
    
    if (!recommendations || recommendations.length === 0) {
        contentElement.textContent = '暂无推荐商品，请稍后再试';
        return;
    }
    
    // 显示推荐商品信息
    if (Array.isArray(recommendations)) {
        // 如果是商品数组，显示前几个商品
        const displayItems = recommendations.slice(0, 3);
        const recommendText = displayItems.map(item => {
            if (typeof item === 'object') {
                return `${item.name || item.productName || '推荐商品'} ¥${item.price || item.productPrice || '价格面议'}`;
            }
            return item.toString();
        }).join('、');
        contentElement.textContent = `为您推荐：${recommendText}`;
    } else if (typeof recommendations === 'object') {
        // 如果是单个对象
        const name = recommendations.name || recommendations.productName || '推荐商品';
        const price = recommendations.price || recommendations.productPrice || '价格面议';
        contentElement.textContent = `为您推荐：${name} ¥${price}`;
    } else {
        // 如果是字符串或其他类型
        contentElement.textContent = `推荐内容：${recommendations}`;
    }
}