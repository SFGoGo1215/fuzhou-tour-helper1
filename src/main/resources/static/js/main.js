   // 初始化小吃卡片
   function initFoodCards(foods) {
       const container = document.getElementById('food-container');
       let html = '';

       foods.forEach(food => {
           html += `
           <div class="food-card" onclick="toggleFoodDetail(this)">
               <div class="food-header">
                   <img src="/images/foods/${food.name}.jpg" alt="${food.name}" class="food-icon">
                   <h3>${food.name}</h3>
               </div>
               <div class="food-detail">
                   <p><strong>制作工艺:</strong> ${food.process}</p>
                   <p><strong>口感特点:</strong> ${food.taste}</p>
                   <p><strong>推荐店铺:</strong> ${food.shop}</p>
               </div>
           </div>`;
       });

       container.innerHTML = html;
   }

   // 点击展开/收起详情
   function toggleFoodDetail(card) {
       card.classList.toggle('active');

       // 自动滚动到最佳查看位置
       if (card.classList.contains('active')) {
           card.scrollIntoView({
               behavior: 'smooth',
               block: 'center'
           });
       }
   }