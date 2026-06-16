(function() {
    const chartData = document.getElementById('chartData');
    if (!chartData) return;

    const d = chartData.dataset;

    const tipoLabels  = JSON.parse(d.tipoLabels  || '[]');
    const tipoCounts  = JSON.parse(d.tipoCounts  || '[]');
    const mesesLabels = JSON.parse(d.mesesLabels || '[]');
    const ingresos    = JSON.parse(d.ingresos    || '[]');
    const salidas     = JSON.parse(d.salidas     || '[]');
    const top5Labels  = JSON.parse(d.top5Labels  || '[]');
    const top5Stock   = JSON.parse(d.top5Stock   || '[]');

    ['graficoDona', 'graficoBarras', 'graficoTop5'].forEach(id => {
        const el = document.getElementById(id);
        if (el && el._apexcharts) {
            el._apexcharts.destroy();
            delete el._apexcharts;
        }
    });

    const elDona = document.getElementById('graficoDona');
    if (elDona) {
        const chartDona = new ApexCharts(elDona, {
            series: tipoCounts,
            labels: tipoLabels,
            chart: {
                type: 'donut',
                height: 260,
                toolbar: { show: false },
                animations: { speed: 600 }
            },
            colors: ['#f0a500', '#1a1a2e', '#198754'],
            plotOptions: {
                pie: {
                    donut: {
                        size: '65%',
                        labels: {
                            show: true,
                            total: {
                                show: true,
                                label: 'Total',
                                fontSize: '13px',
                                color: '#6c757d',
                                formatter: (w) =>
                                    w.globals.seriesTotals.reduce((a, b) => a + b, 0)
                            }
                        }
                    }
                }
            },
            dataLabels: { enabled: false },
            legend: {
                position: 'bottom',
                fontSize: '12px',
                markers: { width: 10, height: 10, radius: 2 }
            },
            tooltip: { style: { fontSize: '12px' } }
        });
        chartDona.render();
        elDona._apexcharts = chartDona;
    }

    const elBarras = document.getElementById('graficoBarras');
    if (elBarras) {
        const chartBarras = new ApexCharts(elBarras, {
            series: [
                { name: 'Ingresos', data: ingresos },
                { name: 'Salidas',  data: salidas  }
            ],
            chart: {
                type: 'bar',
                height: 260,
                toolbar: { show: false },
                animations: { speed: 600 }
            },
            colors: ['#198754', '#dc3545'],
            plotOptions: {
                bar: {
                    columnWidth: '50%',
                    borderRadius: 5,
                    borderRadiusApplication: 'end'
                }
            },
            dataLabels: { enabled: false },
            legend: {
                position: 'top',
                fontSize: '12px',
                markers: { width: 10, height: 10, radius: 2 }
            },
            xaxis: {
                categories: mesesLabels,
                labels: { style: { fontSize: '12px', colors: '#6c757d' } },
                axisBorder: { show: false },
                axisTicks:  { show: false }
            },
            yaxis: {
                labels: {
                    style: { fontSize: '11px', colors: '#6c757d' },
                    formatter: (val) => Math.round(val)
                }
            },
            grid: {
                borderColor: 'rgba(0,0,0,0.06)',
                strokeDashArray: 4
            },
            tooltip: {
                style: { fontSize: '12px' },
                y: { formatter: (val) => val + ' registros' }
            }
        });
        chartBarras.render();
        elBarras._apexcharts = chartBarras;
    }

    const elTop5 = document.getElementById('graficoTop5');
    if (elTop5) {
        const chartTop5 = new ApexCharts(elTop5, {
            series: [{ name: 'Stock actual', data: top5Stock }],
            chart: {
                type: 'bar',
                height: 200,
                toolbar: { show: false },
                animations: { speed: 600 }
            },
            colors: ['#f0a500'],
            plotOptions: {
                bar: {
                    horizontal: true,
                    barHeight: '55%',
                    borderRadius: 5,
                    borderRadiusApplication: 'end'
                }
            },
            dataLabels: {
                enabled: true,
                style: { fontSize: '11px', colors: ['#fff'] },
                formatter: (val) => val + ' uds'
            },
            xaxis: {
                categories: top5Labels,
                labels: {
                    style: { fontSize: '11px', colors: '#6c757d' },
                    formatter: (val) => Math.round(val)
                }
            },
            yaxis: {
                labels: { style: { fontSize: '11px', colors: '#6c757d' } }
            },
            grid: {
                borderColor: 'rgba(0,0,0,0.06)',
                strokeDashArray: 4,
                xaxis: { lines: { show: true } },
                yaxis: { lines: { show: false } }
            },
            tooltip: {
                style: { fontSize: '12px' },
                y: { formatter: (val) => val + ' unidades en stock' }
            },
            legend: { show: false }
        });
        chartTop5.render();
        elTop5._apexcharts = chartTop5;
    }
})();
