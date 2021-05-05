<messageML>
        <table>
            <thead>
            <tr>
                <td>Trade ID</td>
                <td>Status</td>
                <td>State</td>
                <td>Description</td>
                <td>Portfolio</td>
                <td>Price</td>
                <td>Quantity</td>
                <td>Select</td>
            </tr>
            </thead>
            <tbody>
            <#assign trade = trades>
            <tr>
                <td>${trade.id}</td>
                <td>${trade.status}</td>
                <td><span class="tempo-text-color--green">${trade.state}</span></td>
                <td>${trade.description}</td>
                <td>${trade.portfolio}</td>
                <td>${trade.price}</td>
                <td>${trade.quantity}</td>
                <td>${trade.transaction}</td>
            </tr>
            </tbody>
        </table>
</messageML>

