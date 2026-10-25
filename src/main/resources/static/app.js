const WMO={0:['Clear sky','☀️','🌙'],1:['Mainly clear','🌤️','🌙'],2:['Partly cloudy','⛅','☁️'],3:['Overcast','☁️','☁️'],45:['Fog','🌫️'],48:['Rime fog','🌫️'],
51:['Light drizzle','🌦️'],53:['Drizzle','🌦️'],55:['Dense drizzle','🌧️'],56:['Freezing drizzle','🌧️'],57:['Freezing drizzle','🌧️'],61:['Slight rain','🌦️'],63:['Rain','🌧️'],65:['Heavy rain','🌧️'],
66:['Freezing rain','🌧️'],67:['Freezing rain','🌧️'],71:['Slight snow','🌨️'],73:['Snow','🌨️'],75:['Heavy snow','❄️'],77:['Snow grains','🌨️'],80:['Rain showers','🌦️'],81:['Rain showers','🌧️'],82:['Violent showers','⛈️'],
85:['Snow showers','🌨️'],86:['Heavy snow showers','❄️'],95:['Thunderstorm','⛈️'],96:['Thunderstorm, hail','⛈️'],99:['Thunderstorm, heavy hail','⛈️']};
const wx=(c,day=1)=>{const w=WMO[c]||['Unknown','❔'];return{d:w[0],i:(!day&&w[2])?w[2]:w[1]}};
let unit=localStorage.getItem('unit')||'C',last=null,chart=null;
const $=id=>document.getElementById(id);
const T=c=>c==null?'–':Math.round(unit==='C'?c:c*9/5+32)+'°'+unit;
const dir=d=>['N','NE','E','SE','S','SW','W','NW'][Math.round(d/45)%8];
const tm=s=>s.slice(11,16);
async function api(p){const r=await fetch(p);const j=await r.json();if(!r.ok)throw new Error(j.error||r.statusText);return j}
let t;$('q').addEventListener('input',e=>{clearTimeout(t);const v=e.target.value.trim();if(v.length<2){$('suggest').innerHTML='';return}
 t=setTimeout(async()=>{try{const j=await api('/api/search?q='+encodeURIComponent(v));
  $('suggest').innerHTML='';(j.results||[]).forEach(p=>{const li=document.createElement('li');li.textContent=[p.name,p.admin1,p.country].filter(Boolean).join(', ');
   li.onclick=()=>{$('suggest').innerHTML='';$('q').value='';load({name:li.textContent,lat:p.latitude,lon:p.longitude})};$('suggest').appendChild(li)});
  if(!(j.results||[]).length)$('suggest').innerHTML='<li>No matches</li>'}catch(err){status(err.message)}},300)});
$('loc').onclick=()=>{if(!navigator.geolocation)return status('Geolocation not supported');status('Locating…');
 navigator.geolocation.getCurrentPosition(p=>load({name:'My location',lat:p.coords.latitude,lon:p.coords.longitude}),e=>status('Location error: '+e.message))};
$('unit').onclick=()=>{unit=unit==='C'?'F':'C';localStorage.setItem('unit',unit);if(last)render(...last)};
function status(m){$('status').textContent=m||''}
function recents(){const r=JSON.parse(localStorage.getItem('recent')||'[]');$('recent').innerHTML='';r.forEach(p=>{const s=document.createElement('span');s.textContent=p.name;s.onclick=()=>load(p);$('recent').appendChild(s)})}
function save(p){if(p.name==='My location')return;let r=JSON.parse(localStorage.getItem('recent')||'[]').filter(x=>x.name!==p.name);r.unshift(p);localStorage.setItem('recent',JSON.stringify(r.slice(0,6)));recents()}
async function load(p){status('Loading '+p.name+'…');try{const[f,a]=await Promise.all([api(`/api/forecast?lat=${p.lat}&lon=${p.lon}`),api(`/api/air-quality?lat=${p.lat}&lon=${p.lon}`).catch(()=>null)]);
 save(p);localStorage.setItem('lastPlace',JSON.stringify(p));last=[p,f,a];render(p,f,a);status('')}catch(e){status('Error: '+e.message)}}
function render(p,f,a){const c=f.current,w=wx(c.weather_code,c.is_day),d=f.daily,h=f.hourly;
 $('current').innerHTML=`<h2>${p.name} <small style="color:#778;font-weight:400">${f.timezone} · updated ${tm(c.time)}</small></h2><div class="cur"><div class="icon">${w.i}</div><div><div class="big">${T(c.temperature_2m)}</div><div>${w.d} · feels like ${T(c.apparent_temperature)}</div><div>H ${T(d.temperature_2m_max[0])} / L ${T(d.temperature_2m_min[0])}</div></div>
 <div class="stats">${[['Humidity',c.relative_humidity_2m+'%'],['Wind',`${c.wind_speed_10m} km/h ${dir(c.wind_direction_10m)}`],['Gusts',c.wind_gusts_10m+' km/h'],['Precipitation',c.precipitation+' mm'],['Rain chance',(d.precipitation_probability_max[0]??'–')+'%'],['UV index',c.uv_index??d.uv_index_max[0]],['Pressure',Math.round(c.pressure_msl)+' hPa'],['Clouds',c.cloud_cover+'%'],['Sunrise',tm(d.sunrise[0])],['Sunset',tm(d.sunset[0])]].map(([k,v])=>`<div class="stat"><b>${k}</b>${v}</div>`).join('')}</div></div>`;
 const cv=x=>x==null?null:(unit==='C'?x:x*9/5+32);
 if(window.Chart){chart&&chart.destroy();chart=new Chart($('chart'),{data:{labels:h.time.map(s=>s.slice(5,10)+' '+tm(s)),datasets:[
  {type:'line',label:'Temp °'+unit,data:h.temperature_2m.map(cv),borderColor:'#f57c00',tension:.3,yAxisID:'y'},
  {type:'line',label:'Feels like °'+unit,data:h.apparent_temperature.map(cv),borderColor:'#ffb74d',borderDash:[4,4],tension:.3,yAxisID:'y'},
  {type:'bar',label:'Rain %',data:h.precipitation_probability,backgroundColor:'#42a5f566',yAxisID:'p'}]},
  options:{maintainAspectRatio:false,animation:false,scales:{p:{position:'right',min:0,max:100,grid:{display:false}}}}})}
 $('hourly').innerHTML=h.time.map((s,i)=>{const x=wx(h.weather_code[i],h.is_day[i]);return`<div class="hr">${tm(s)}<div style="font-size:22px">${x.i}</div>${T(h.temperature_2m[i])}<br>💧${h.precipitation_probability[i]??0}%<br>💨${Math.round(h.wind_speed_10m[i])}</div>`}).join('');
 $('daily').innerHTML=d.time.map((s,i)=>{const x=wx(d.weather_code[i]);const n=new Date(s+'T00:00').toLocaleDateString(undefined,{weekday:'short',day:'numeric',month:'short'});
  return`<div class="day"><b>${i?n:'Today'}</b><div class="i">${x.i}</div>${x.d}<br><b>${T(d.temperature_2m_max[i])}</b> / ${T(d.temperature_2m_min[i])}<br>💧${d.precipitation_probability_max[i]??0}% · ${d.precipitation_sum[i]} mm<br>💨${Math.round(d.wind_speed_10m_max[i])} km/h ${dir(d.wind_direction_10m_dominant[i])}<br>UV ${d.uv_index_max[i]??'–'}<br>🌅${tm(d.sunrise[i])} 🌇${tm(d.sunset[i])}</div>`}).join('');
 if(a&&a.current){const q=a.current,v=q.us_aqi,L=v<=50?['Good','#43a047']:v<=100?['Moderate','#fbc02d']:v<=150?['Unhealthy for sensitive groups','#fb8c00']:v<=200?['Unhealthy','#e53935']:v<=300?['Very unhealthy','#8e24aa']:['Hazardous','#6d1b1b'];
  $('aq').innerHTML=`<span class="aqi" style="background:${L[1]}">US AQI ${v} · ${L[0]}</span> <span style="margin-left:8px">European AQI ${q.european_aqi}</span><div class="stats" style="margin-top:10px">${[['PM2.5',q.pm2_5+' µg/m³'],['PM10',q.pm10+' µg/m³'],['O₃',q.ozone+' µg/m³'],['NO₂',q.nitrogen_dioxide+' µg/m³'],['SO₂',q.sulphur_dioxide+' µg/m³'],['CO',q.carbon_monoxide+' µg/m³']].map(([k,v])=>`<div class="stat"><b>${k}</b>${v}</div>`).join('')}</div>`;$('aqCard').classList.remove('hidden')}else $('aqCard').classList.add('hidden');
 ['current','hourlyCard','dailyCard'].forEach(i=>$(i).classList.remove('hidden'))}
recents();load(JSON.parse(localStorage.getItem('lastPlace')||'null')||{name:'Pune, Maharashtra, India',lat:18.51957,lon:73.85535});
